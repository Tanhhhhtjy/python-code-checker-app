package com.example.codechecker.ui.screens.main

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.clickable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.codechecker.ui.screens.main.MainViewModel
import com.example.codechecker.data.model.Session

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onLogout: () -> Unit,
    onNavigateToFileList: () -> Unit,
    onNavigateToUpload: () -> Unit,
    onNavigateToPlagiarism: () -> Unit,
    onNavigateToProfile: () -> Unit,
    mainViewModel: MainViewModel
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("首页") },
                actions = {
                    IconButton(onClick = onNavigateToProfile) {
                        Icon(
                            Icons.Default.Person,
                            contentDescription = "个人资料",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    Button(
                        onClick = onLogout,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.error,
                            contentColor = MaterialTheme.colorScheme.onError
                        )
                    ) {
                        Text("登出")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            Text(
                text = "欢迎使用 CodeChecker",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            
            // 显示用户信息
            val currentSession = mainViewModel.appState.value.currentSession
            currentSession?.let { session ->
                UserInfoCard(
                    session = session,
                    onProfileClick = onNavigateToProfile  // 点击用户信息也可进入个人资料
                )
            }
            
            // 功能卡片网格
            Text(
                text = "功能列表",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp, bottom = 16.dp)
            )
            
            // 功能网格布局
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // 上传代码卡片
                    FunctionCard(
                        title = "上传代码",
                        description = "上传源代码文件进行查重检测",
                        icon = Icons.Default.Upload,
                        onClick = onNavigateToUpload,
                        modifier = Modifier.weight(1f)
                    )
                    
                    // 文件列表卡片
                    FunctionCard(
                        title = "文件管理",
                        description = "查看和管理已上传的文件",
                        icon = Icons.Default.Folder,
                        onClick = onNavigateToFileList,
                        modifier = Modifier.weight(1f)
                    )
                }
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // 查重检测卡片
                    FunctionCard(
                        title = "查重检测",
                        description = "进行代码相似度检测",
                        icon = Icons.Default.Search,
                        onClick = onNavigateToPlagiarism,
                        modifier = Modifier.weight(1f)
                    )
                    
                    // 用户资料卡片
                    FunctionCard(
                        title = "个人资料",
                        description = "查看和修改个人信息",
                        icon = Icons.Default.Person,
                        onClick = onNavigateToProfile,
                        modifier = Modifier.weight(1f)
                    )
                }
                
                // 如果用户是教师，可以添加额外功能
                if (currentSession?.role == "TEACHER") {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // 教师专属功能卡片（示例）
                        FunctionCard(
                            title = "学生管理",
                            description = "管理学生账户和作业",
                            icon = Icons.Default.Group,
                            onClick = { /* TODO: 添加学生管理导航 */ },
                            modifier = Modifier.weight(1f),
                            containerColor = MaterialTheme.colorScheme.primaryContainer
                        )
                        
                        // 统计分析卡片
                        FunctionCard(
                            title = "统计分析",
                            description = "查看查重统计报告",
                            icon = Icons.Default.Analytics,
                            onClick = { /* TODO: 添加统计分析导航 */ },
                            modifier = Modifier.weight(1f),
                            containerColor = MaterialTheme.colorScheme.primaryContainer
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun UserInfoCard(
    session: Session,
    onProfileClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onProfileClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "用户信息",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            // 个人资料图标提示
            Icon(
                Icons.Default.ChevronRight,
                contentDescription = "查看详情",
                tint = MaterialTheme.colorScheme.primary
            )
            
            InfoRow(label = "用户名:", value = session.username)
            Spacer(modifier = Modifier.height(8.dp))
            InfoRow(label = "显示名称:", value = session.displayName)
            Spacer(modifier = Modifier.height(8.dp))
            InfoRow(
                label = "角色:",
                value = if (session.role == "TEACHER") "教师" else "学生",
                valueColor = if (session.role == "TEACHER") 
                    MaterialTheme.colorScheme.primary 
                else 
                    MaterialTheme.colorScheme.secondary
            )
        }
    }
}

@Composable
fun InfoRow(
    label: String,
    value: String,
    valueColor: androidx.compose.ui.graphics.Color = MaterialTheme.colorScheme.onSurface
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold,
            color = valueColor
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FunctionCard(
    title: String,
    description: String,
    icon: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    containerColor: androidx.compose.ui.graphics.Color = MaterialTheme.colorScheme.surface
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(150.dp),
        onClick = onClick,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = containerColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(40.dp)
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center
            )
            
            Spacer(modifier = Modifier.height(4.dp))
            
            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                maxLines = 2
            )
        }
    }
}