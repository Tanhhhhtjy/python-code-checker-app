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
    
    Scaffold(
        topBar = {
            // 添加顶部导航栏
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
                    // 点击空白处收起键盘
                    focusManager.clearFocus()

                    // 文件名输入框失去焦点时自动添加.py后缀
                    if (!isFileNameFocused && fileName.isNotBlank() && !fileName.endsWith(".py", ignoreCase = true)) {
                        fileName += ".py"
                    }
                }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()), // 添加滚动支持
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
                
                // 成功提示
                if (uiState.value.showUploadSuccess) {
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer
                        ),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Upload,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("文件上传成功！")
                        }
                    }
                }
                
                // 文件名输入
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
                            // 当失去焦点且文件名不为空且没有.py后缀时，自动添加
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
                    placeholder = { Text("用于显示的友好名称") },
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
                
                // 上传按钮
                Button(
                    onClick = {
                        // 收起键盘
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
                        
                        viewModel.uploadFile(
                            fileName = finalFileName,
                            codeContent = codeContent,
                            displayName = if (displayName.isBlank()) null else displayName
                        )
                        
                        // 清空表单（可选）
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
                
                // 监听上传成功
                LaunchedEffect(uiState.value.showUploadSuccess) {
                    if (uiState.value.showUploadSuccess) {
                        // 延迟执行成功回调
                        kotlinx.coroutines.delay(1500)
                        onUploadSuccess()
                    }
                }
            }
        }
    }
}