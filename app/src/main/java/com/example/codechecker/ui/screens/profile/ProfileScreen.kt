package com.example.codechecker.ui.screens.profile

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.codechecker.ui.screens.main.MainViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    onNavigateBack: () -> Unit,
    onLogout: () -> Unit,
    onNavigateToChangePassword: () -> Unit,
    mainViewModel: MainViewModel
) {
    val currentSession = mainViewModel.appState.value.currentSession
    val coroutineScope = rememberCoroutineScope()
    
    if (currentSession == null) {
        // 如果没有登录信息，返回
        onNavigateBack()
        return
    }
    
    val isTeacher = currentSession.role == "TEACHER"
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("个人资料") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "返回")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
        ) {
            // 用户头像和信息卡片
            ProfileHeaderCard(
                session = currentSession,
                isTeacher = isTeacher,
                modifier = Modifier.padding(16.dp)
            )
            
            // 基本信息部分
            ProfileSection(
                title = "基本信息",
                items = listOf(
                    ProfileItem(
                        icon = Icons.Default.Person,
                        label = "用户名",
                        value = currentSession.username,
                        onClick = { /* 用户名通常不可编辑 */ }
                    ),
                    ProfileItem(
                        icon = Icons.Default.Badge,
                        label = "显示名称",
                        value = currentSession.displayName,
                        onClick = { /* TODO: 编辑显示名称 */ }
                    ),
                    ProfileItem(
                        icon = if (isTeacher) Icons.Default.School else Icons.Default.Person,
                        label = "角色",
                        value = if (isTeacher) "教师" else "学生",
                        onClick = { /* 角色不可更改 */ }
                    ),
                    ProfileItem(
                        icon = Icons.Default.Email,
                        label = "邮箱",
                        value = currentSession.email ?: "未设置",
                        onClick = { /* TODO: 编辑邮箱 */ }
                    )
                )
            )
            
            // 安全设置部分
            ProfileSection(
                title = "安全设置",
                items = listOf(
                    ProfileItem(
                        icon = Icons.Default.Lock,
                        label = "修改密码",
                        value = "",
                        onClick = onNavigateToChangePassword
                    )
                )
            )
            
            // 使用统计（如果是教师，显示更多统计）
            ProfileSection(
                title = "使用统计",
                items = buildList {
                    add(
                        ProfileItem(
                            icon = Icons.Default.Folder,
                            label = "上传文件数",
                            value = "统计中...",
                            onClick = { /* TODO: 查看文件列表 */ }
                        )
                    )
                    add(
                        ProfileItem(
                            icon = Icons.Default.Search,
                            label = "查重次数",
                            value = "统计中...",
                            onClick = { /* TODO: 查看查重历史 */ }
                        )
                    )
                    
                    if (isTeacher) {
                        add(
                            ProfileItem(
                                icon = Icons.Default.Group,
                                label = "学生数量",
                                value = "统计中...",
                                onClick = { /* TODO: 查看学生列表 */ }
                            )
                        )
                    }
                }
            )
            
            // 系统设置部分
            ProfileSection(
                title = "系统设置",
                items = listOf(
                    ProfileItem(
                        icon = Icons.Default.Notifications,
                        label = "通知设置",
                        value = "",
                        onClick = { /* TODO: 通知设置 */ }
                    ),
                    ProfileItem(
                        icon = Icons.Default.Palette,
                        label = "主题设置",
                        value = "",
                        onClick = { /* TODO: 主题设置 */ }
                    ),
                    ProfileItem(
                        icon = Icons.Default.Info,
                        label = "关于应用",
                        value = "版本 1.0.0",
                        onClick = { /* TODO: 关于页面 */ }
                    )
                )
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // 操作按钮区域
            Column(
                modifier = Modifier.padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // 登出按钮
                Button(
                    onClick = onLogout,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error,
                        contentColor = MaterialTheme.colorScheme.onError
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.Logout, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("退出登录")
                }
                
                // 删除账户按钮（危险操作）
                OutlinedButton(
                    onClick = {
                        // TODO: 显示确认对话框
                    },
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = MaterialTheme.colorScheme.error
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.Delete, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("删除账户")
                }
            }
            
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
fun ProfileHeaderCard(
    session: com.example.codechecker.domain.model.Session,
    isTeacher: Boolean,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 头像（暂时用图标代替）
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .padding(bottom = 16.dp),
                contentAlignment = Alignment.Center
            ) {
                if (isTeacher) {
                    Icon(
                        Icons.Default.School,
                        contentDescription = null,
                        modifier = Modifier.size(48.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                } else {
                    Icon(
                        Icons.Default.Person,
                        contentDescription = null,
                        modifier = Modifier.size(48.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
                
                // 编辑头像按钮（悬浮）
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .size(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    FloatingActionButton(
                        onClick = { /* TODO: 编辑头像 */ },
                        modifier = Modifier.size(32.dp),
                        containerColor = MaterialTheme.colorScheme.primary
                    ) {
                        Icon(
                            Icons.Default.Edit,
                            contentDescription = "编辑头像",
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
            
            Text(
                text = session.displayName,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            
            Text(
                text = "@${session.username}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.outline
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // 角色徽章
            Chip(
                onClick = { },
                label = { 
                    Text(if (isTeacher) "教师" else "学生") 
                },
                colors = ChipDefaults.chipColors(
                    containerColor = if (isTeacher) 
                        MaterialTheme.colorScheme.primary 
                    else 
                        MaterialTheme.colorScheme.secondary,
                    labelColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    }
}

@Composable
fun ProfileSection(
    title: String,
    items: List<ProfileItem>,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
        )
        
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column {
                items.forEachIndexed { index, item ->
                    ProfileItemRow(
                        item = item,
                        showDivider = index < items.size - 1
                    )
                }
            }
        }
    }
}

@Composable
fun ProfileItemRow(
    item: ProfileItem,
    showDivider: Boolean
) {
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp)
                .clickable { item.onClick() },
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                item.icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = item.label,
                    style = MaterialTheme.typography.bodyMedium
                )
                
                if (item.value.isNotBlank()) {
                    Text(
                        text = item.value,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.outline
                    )
                }
            }
            
            Icon(
                Icons.Default.ChevronRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.outline
            )
        }
        
        if (showDivider) {
            Divider(
                modifier = Modifier.padding(horizontal = 16.dp),
                thickness = 0.5.dp
            )
        }
    }
}

data class ProfileItem(
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    val label: String,
    val value: String,
    val onClick: () -> Unit
)