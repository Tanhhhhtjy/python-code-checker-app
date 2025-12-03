package com.example.codechecker.ui.screens.code

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Upload
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.codechecker.ui.screens.code.FileViewModel
import com.example.codechecker.ui.screens.code.FileViewModelFactory
import com.example.codechecker.CodeCheckerApp

@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
@Composable
fun CodeUploadScreen(
    onUploadSuccess: () -> Unit = {},
    onNavigateToHome: () -> Unit = {},
    viewModelFactory: FileViewModelFactory,
    viewModel: FileViewModel = viewModel(
        factory = FileViewModelFactory(CodeCheckerApp.sessionManager)
    )
) {
    var fileName by remember { mutableStateOf("") }
    var displayName by remember { mutableStateOf("") }
    var codeContent by remember { mutableStateOf("") }
    val uiState = viewModel.uiState.collectAsState()
    
    // 获取焦点管理器
    val focusManager = LocalFocusManager.current

    // 用于跟踪文件名输入框的焦点状态
    val fileNameFocusRequester = remember { FocusRequester() }
    var isFileNameFocused by remember { mutableStateOf(false) }
    
    // 监听上传成功，显示重命名信息
    val uploadInfo = uiState.value.uploadResultInfo
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("上传代码") },
                navigationIcon = {
                    IconButton(onClick = onNavigateToHome) {
                        Icon(Icons.Default.Home, contentDescription = "返回首页")
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .clickable(
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() }
                ) {
                    focusManager.clearFocus()
                    if (!isFileNameFocused && fileName.isNotBlank() && !fileName.endsWith(".py", ignoreCase = true)) {
                        fileName += ".py"
                    }
                }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // 标题
                Text(
                    text = "上传Python代码",
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.primary
                )
                
                // 错误提示
                uiState.value.error?.let { error ->
                    Text(
                        text = error,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
                
                // 上传成功提示（包含重命名信息）
                if (uiState.value.showUploadSuccess && uploadInfo != null) {
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer
                        ),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Upload,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "文件上传成功！",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            
                            Spacer(modifier = Modifier.height(8.dp))
                            
                            // 显示上传详细信息
                            Column {
                                Text(
                                    text = "显示名称: ${uploadInfo.displayName}",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                                
                                if (uploadInfo.wasRenamed) {
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "⚠️ 文件名已自动重命名",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.error
                                    )
                                    Text(
                                        text = "输入: ${uploadInfo.originalFileName}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.outline
                                    )
                                    Text(
                                        text = "保存为: ${uploadInfo.systemFileName}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.outline
                                    )
                                } else {
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "文件名: ${uploadInfo.systemFileName}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.outline
                                    )
                                }
                            }
                        }
                    }
                }
                
                // 文件名输入 - 添加重名提示
                Column {
                    OutlinedTextField(
                        value = fileName,
                        onValueChange = { fileName = it },
                        label = { Text("文件名（例如：main.py）") },
                        leadingIcon = {
                            Icon(Icons.Default.Description, contentDescription = null)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .focusRequester(fileNameFocusRequester)
                            .onFocusChanged { focusState ->
                                isFileNameFocused = focusState.isFocused
                                if (!focusState.isFocused && fileName.isNotBlank() && 
                                    !fileName.endsWith(".py", ignoreCase = true)) {
                                    fileName += ".py"
                                }
                            },
                        singleLine = true,
                        placeholder = { Text("输入.py文件名") },
                        keyboardOptions = KeyboardOptions.Default.copy(
                            keyboardType = KeyboardType.Text
                        )
                    )
                    
                    // 添加文件名提示
                    if (fileName.isNotBlank() && !fileName.endsWith(".py", ignoreCase = true)) {
                        Text(
                            text = "提示：将自动添加 .py 后缀",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.tertiary,
                            modifier = Modifier.padding(start = 4.dp, top = 4.dp)
                        )
                    }
                }
                
                // 显示名称输入
                OutlinedTextField(
                    value = displayName,
                    onValueChange = { displayName = it },
                    label = { Text("显示名称（可选）") },
                    leadingIcon = {
                        Icon(Icons.Default.Description, contentDescription = null)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    placeholder = { 
                        Text(
                            if (fileName.isNotBlank()) 
                                "如不填写，将使用: ${fileName.substringBeforeLast(".")}" 
                            else "用于显示的友好名称"
                        )
                    },
                    keyboardOptions = KeyboardOptions.Default.copy(
                        keyboardType = KeyboardType.Text
                    )
                )
                
                // 代码内容输入
                OutlinedTextField(
                    value = codeContent,
                    onValueChange = { codeContent = it },
                    label = { Text("Python代码") },
                    leadingIcon = {
                        Icon(Icons.Default.Code, contentDescription = null)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp),
                    singleLine = false,
                    maxLines = Int.MAX_VALUE,
                    keyboardOptions = KeyboardOptions.Default.copy(
                        keyboardType = KeyboardType.Text
                    )
                )
                
                // 代码信息
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "行数: ${codeContent.lines().size}",
                        style = MaterialTheme.typography.bodySmall
                    )
                    Text(
                        text = "字符数: ${codeContent.length}",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // 上传按钮 - 添加重名提示
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (fileName.isNotBlank()) {
                        // 显示文件名预览
                        val finalDisplayName = if (displayName.isBlank()) {
                            fileName.substringBeforeLast(".")
                        } else {
                            displayName
                        }
                        
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant
                            ),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier.padding(12.dp)
                            ) {
                                Text(
                                    text = "上传预览",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.outline
                                )
                                Text(
                                    text = "显示名称: $finalDisplayName",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                                Text(
                                    text = "系统将自动检查重名并处理",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.tertiary
                                )
                            }
                        }
                    }
                    
                    Button(
                        onClick = {
                            focusManager.clearFocus()
                            
                            if (fileName.isBlank() || codeContent.isBlank()) {
                                return@Button
                            }
                            
                            // 确保文件名以.py结尾
                            val finalFileName = if (!fileName.endsWith(".py", ignoreCase = true)) {
                                "$fileName.py"
                            } else {
                                fileName
                            }
                            
                            // 设置显示名称（如果用户没输入，使用文件名去掉后缀）
                            val finalDisplayName = if (displayName.isBlank()) {
                                finalFileName.substringBeforeLast(".")
                            } else {
                                displayName
                            }
                            
                            viewModel.uploadFile(
                                fileName = finalFileName,
                                codeContent = codeContent,
                                displayName = finalDisplayName
                            )
                            
                            // 清空表单
                            fileName = ""
                            displayName = ""
                            codeContent = ""
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        enabled = !uiState.value.isUploading &&
                                 fileName.isNotBlank() &&
                                 codeContent.isNotBlank()
                    ) {
                        if (uiState.value.isUploading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                        } else {
                            Icon(Icons.Default.Upload, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("上传代码")
                        }
                    }
                }
                
                // 监听上传成功
                LaunchedEffect(uiState.value.showUploadSuccess) {
                    if (uiState.value.showUploadSuccess) {
                        // 延迟执行成功回调，给用户时间看清重命名信息
                        kotlinx.coroutines.delay(1500)
                        onUploadSuccess()
                    }
                }
            }
        }
    }
}