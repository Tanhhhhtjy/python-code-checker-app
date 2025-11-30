package com.example.codechecker.ui.screens.auth

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.codechecker.domain.model.UserRole
import com.example.codechecker.ui.screens.auth.AuthViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(
    onRegisterSuccess: () -> Unit,
    onNavigateToLogin: () -> Unit,
    viewModel: AuthViewModel = viewModel()
) {
    val registerState = remember { mutableStateOf(RegisterState()) }
    var confirmPassword by remember { mutableStateOf("") }
    val focusManager = LocalFocusManager.current

    // 手动收集 StateFlow 的值
    LaunchedEffect(viewModel.registerState) {
        viewModel.registerState.collect { state ->
            registerState.value = state
        }
    }

    // 监听注册成功
    LaunchedEffect(registerState.value.isSuccess) {
        if (registerState.value.isSuccess) {
            onRegisterSuccess()
        }
    }

    Scaffold { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .clickable(indication = null, interactionSource = remember { MutableInteractionSource() }) {
                    // 点击空白处收起键盘
                    focusManager.clearFocus()
                },
            contentAlignment = Alignment.Center
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()), // 键盘滚动支持
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // 显示错误消息
                    if (registerState.value.errorMessage != null) {
                        Text(
                            text = registerState.value.errorMessage ?: "",
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodyMedium,
                            textAlign = TextAlign.Center
                        )
                    }

                    // 标题
                    Text(
                        text = "用户注册",
                        style = MaterialTheme.typography.headlineLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // 显示名称输入
                    OutlinedTextField(
                        value = registerState.value.displayName,
                        onValueChange = viewModel::updateRegisterDisplayName,
                        label = { Text("显示名称") },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = null
                            )
                        },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        placeholder = { Text("例如：张三") }
                    )

                    // 用户名输入
                    OutlinedTextField(
                        value = registerState.value.username,
                        onValueChange = viewModel::updateRegisterUsername,
                        label = { Text("用户名") },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.AccountCircle,
                                contentDescription = null
                            )
                        },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        placeholder = { Text("用于登录的用户名") }
                    )

                    // 密码输入
                    OutlinedTextField(
                        value = registerState.value.password,
                        onValueChange = viewModel::updateRegisterPassword,
                        label = { Text("密码") },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Lock,
                                contentDescription = null
                            )
                        },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        visualTransformation = PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions.Default.copy(
                            keyboardType = KeyboardType.Password
                        ),
                        placeholder = { Text("至少6位字符") }
                    )

                    // 确认密码输入
                    OutlinedTextField(
                        value = confirmPassword,
                        onValueChange = { confirmPassword = it },
                        label = { Text("确认密码") },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Lock,
                                contentDescription = null
                            )
                        },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        visualTransformation = PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions.Default.copy(
                            keyboardType = KeyboardType.Password
                        )
                    )

                    // 角色选择
                    Text(
                        text = "选择角色",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // 学生选项
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.clickable { 
                                viewModel.updateRegisterRole(UserRole.STUDENT) 
                            }
                        ) {
                            RadioButton(
                                selected = registerState.value.role == UserRole.STUDENT,
                                onClick = { viewModel.updateRegisterRole(UserRole.STUDENT) }
                            )
                            Text(
                                text = "学生",
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.padding(start = 8.dp)
                            )
                        }
    
                        // 教师选项
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.clickable { 
                                viewModel.updateRegisterRole(UserRole.TEACHER) 
                            }
                        ) {
                            RadioButton(
                                selected = registerState.value.role == UserRole.TEACHER,
                                onClick = { viewModel.updateRegisterRole(UserRole.TEACHER) }
                            )
                            Text(
                                text = "教师",
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.padding(start = 8.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // 注册按钮
                    Button(
                        onClick = {
                            // 密码确认检查
                            if (registerState.value.password != confirmPassword) {
                                viewModel.updateRegisterError("两次输入的密码不一致")
                                return@Button
                            }

                            // 基本验证
                            if (registerState.value.displayName.isBlank() ||
                                registerState.value.username.isBlank() ||
                                registerState.value.password.isBlank()
                            ) {
                                viewModel.updateRegisterError("请填写所有必填字段")
                                return@Button
                            }

                            if (registerState.value.password.length < 6) {
                                viewModel.updateRegisterError("密码长度至少为6位")
                                return@Button
                            }

                            viewModel.register(
                                username = registerState.value.username,
                                password = registerState.value.password,
                                displayName = registerState.value.displayName,
                                role = registerState.value.role
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        enabled = !registerState.value.isLoading
                    ) {
                        if (registerState.value.isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                        } else {
                            Text(
                                text = "注册",
                                style = MaterialTheme.typography.labelLarge
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // 登录链接
                    TextButton(onClick = onNavigateToLogin) {
                        Text("已有账号？立即登录")
                    }
                }
            }
        }
    }
}