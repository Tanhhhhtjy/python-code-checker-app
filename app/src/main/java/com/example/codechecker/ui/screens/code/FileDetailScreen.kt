package com.example.codechecker.ui.screens.code

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.codechecker.CodeCheckerApp
import com.example.codechecker.domain.model.CodeFile
import com.example.codechecker.ui.screens.main.MainViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FileDetailScreen(
    fileId: Long,
    onNavigateBack: () -> Unit,
    onNavigateToPlagiarism: (Long) -> Unit = {},
    mainViewModel: MainViewModel, // 用于获取用户身份
    viewModelFactory: FileViewModelFactory,
    viewModel: FileViewModel = viewModel(
        factory = FileViewModelFactory(CodeCheckerApp.sessionManager)
    )
) {
    val uiState by viewModel.uiState.collectAsState()
    val coroutineScope = rememberCoroutineScope()
    val clipboardManager = LocalClipboardManager.current
    
    // 获取当前用户身份
    val currentSession = mainViewModel.appState.value.currentSession
    val isTeacher = currentSession?.role == "TEACHER"
    
    // 获取特定文件
    val currentFile = viewModel.files.collectAsState(initial = emptyList())
        .value
        .find { it.id == fileId }
    
    // 如果没有找到文件，显示加载状态
    if (currentFile == null) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
        return
    }
    
    // 判断是否是自己的文件（学生只能操作自己的文件）
    val isOwnFile = currentFile.userId == currentSession?.userId
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        text = currentFile.displayName,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "返回")
                    }
                },
                actions = {
                    // 教师可以添加批注
                    if (isTeacher) {
                        IconButton(onClick = { 
                            // TODO: 实现批注功能
                        }) {
                            Icon(Icons.Default.Comment, contentDescription = "添加批注")
                        }
                    }
                    
                    // 学生可以编辑自己的文件，教师可以编辑所有文件
                    if (!isTeacher && isOwnFile || isTeacher) {
                        IconButton(onClick = { 
                            // TODO: 实现编辑功能
                        }) {
                            Icon(Icons.Default.Edit, contentDescription = "编辑")
                        }
                    }
                    
                    // 查重按钮（学生只能查自己的文件，教师可以查所有文件）
                    if (!isTeacher && isOwnFile || isTeacher) {
                        IconButton(
                            onClick = { onNavigateToPlagiarism(fileId) },
                            enabled = !uiState.isLoading
                        ) {
                            Icon(Icons.Default.Search, contentDescription = "查重检测")
                        }
                    }
                    
                    // 删除按钮（学生只能删自己的文件，教师可以删所有文件）
                    if (!isTeacher && isOwnFile || isTeacher) {
                        IconButton(
                            onClick = {
                                coroutineScope.launch {
                                    viewModel.deleteFile(fileId)
                                    // 删除成功后返回
                                    onNavigateBack()
                                }
                            }
                        ) {
                            Icon(
                                Icons.Default.Delete,
                                contentDescription = "删除",
                                tint = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                }
            )
        },
        floatingActionButton = {
            // 学生：查重检测（只能查自己的文件）
            if (!isTeacher && isOwnFile) {
                ExtendedFloatingActionButton(
                    onClick = { onNavigateToPlagiarism(fileId) },
                    icon = { Icon(Icons.Default.Search, "查重") },
                    text = { Text("进行查重检测") },
                    containerColor = MaterialTheme.colorScheme.primary
                )
            }
            // 教师：批量操作或对比功能
            else if (isTeacher) {
                ExtendedFloatingActionButton(
                    onClick = { 
                        // TODO: 教师批量操作
                    },
                    icon = { Icon(Icons.Default.CompareArrows, "对比") },
                    text = { Text("对比代码") },
                    containerColor = MaterialTheme.colorScheme.primary
                )
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // 文件信息卡片
            item {
                FileInfoCard(file = currentFile, isTeacher = isTeacher, isOwnFile = isOwnFile)
            }
            
            // 教师看到的额外信息（学生文件的所有者信息）
            if (isTeacher && !isOwnFile) {
                item {
                    StudentInfoCard(file = currentFile)
                }
            }
            
            // 代码内容
            item {
                CodeContentCard(
                    file = currentFile,
                    onCopyCode = {
                        clipboardManager.setText(AnnotatedString(currentFile.codeContent))
                        // TODO: 显示复制成功的Snackbar
                    }
                )
            }
            
            // 查重历史（如果有）
            if (uiState.plagiarismHistory.isNotEmpty() && (!isTeacher || isOwnFile)) {
                item {
                    PlagiarismHistoryCard(history = uiState.plagiarismHistory)
                }
            }
            
            // 危险区域（学生只能看到自己的危险区域）
            if ((!isTeacher && isOwnFile) || (isTeacher && isOwnFile)) {
                item {
                    DangerZoneCard(
                        fileId = fileId,
                        onDelete = {
                            coroutineScope.launch {
                                viewModel.deleteFile(fileId)
                                onNavigateBack()
                            }
                        },
                        onReupload = {
                            // TODO: 实现重新上传功能
                        }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FileInfoCard(file: CodeFile, isTeacher: Boolean, isOwnFile: Boolean) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "文件信息",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                
                // 如果是教师查看学生文件，显示标识
                if (isTeacher && !isOwnFile) {
                    AssistChip(
                        onClick = { },
                        label = { Text("学生作业") },
                        colors = AssistChipDefaults.assistChipColors(
                            containerColor = MaterialTheme.colorScheme.secondaryContainer,
                            labelColor = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            InfoRow(label = "显示名称:", value = file.displayName)
            Spacer(modifier = Modifier.height(8.dp))
            InfoRow(label = "文件名:", value = file.fileName)
            Spacer(modifier = Modifier.height(8.dp))
            InfoRow(label = "文件大小:", value = file.formattedSize)
            Spacer(modifier = Modifier.height(8.dp))
            InfoRow(label = "行数:", value = file.lineCount.toString())
            Spacer(modifier = Modifier.height(8.dp))
            InfoRow(label = "上传时间:", value = file.uploadedAt.toString())
            Spacer(modifier = Modifier.height(8.dp))
            InfoRow(label = "最后修改:", value = file.lastModifiedAt.toString())
        }
    }
}

@Composable
fun InfoRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.outline
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
fun StudentInfoCard(file: CodeFile) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                Icons.Default.Person,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSecondaryContainer
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = "学生提交",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSecondaryContainer
                )
                Text(
                    text = "学生ID: ${file.userId}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSecondaryContainer
                )
            }
        }
    }
}

@Composable
fun CodeContentCard(file: CodeFile, onCopyCode: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "代码内容",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                
                IconButton(onClick = onCopyCode) {
                    Icon(Icons.Default.ContentCopy, "复制代码")
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            SelectionContainer {
                Text(
                    text = file.codeContent,
                    fontFamily = FontFamily.Monospace,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // 代码统计
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "行数",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.outline
                    )
                    Text(
                        text = file.lineCount.toString(),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
                
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "字符数",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.outline
                    )
                    Text(
                        text = file.codeContent.length.toString(),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
                
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "文件大小",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.outline
                    )
                    Text(
                        text = file.formattedSize,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
fun DangerZoneCard(fileId: Long, onDelete: () -> Unit, onReupload: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.errorContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "危险操作",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onErrorContainer,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Button(
                onClick = onDelete,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.error,
                    contentColor = MaterialTheme.colorScheme.onError
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Default.Delete, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("删除此文件")
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            OutlinedButton(
                onClick = onReupload,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = MaterialTheme.colorScheme.onErrorContainer
                )
            ) {
                Icon(Icons.Default.Upload, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("重新上传")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlagiarismHistoryCard(history: List<Any>) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "查重历史",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // 这里可以显示查重历史记录
            history.forEachIndexed { index, item ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("查重检测", style = MaterialTheme.typography.bodyMedium)
                            Text("2024-01-15 14:30", 
                                 style = MaterialTheme.typography.bodySmall,
                                 color = MaterialTheme.colorScheme.outline)
                        }
                        
                        AssistChip(
                            onClick = { },
                            label = { Text("相似度: 85%") },
                            colors = AssistChipDefaults.assistChipColors(
                                containerColor = MaterialTheme.colorScheme.error,
                                labelColor = MaterialTheme.colorScheme.onError
                            )
                        )
                    }
                }
            }
        }
    }
}