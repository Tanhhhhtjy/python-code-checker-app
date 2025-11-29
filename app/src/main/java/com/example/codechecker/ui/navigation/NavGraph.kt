package com.example.codechecker.ui.navigation

import androidx.compose.runtime.Composable
import com.example.codechecker.ui.screens.auth.LoginScreen

@Composable
fun NavGraph() {
    // 简化版本：直接显示登录页面
    // 等项目构建成功后，我们再逐步添加导航功能
    LoginScreen(
        onLoginSuccess = {
            // TODO: 登录成功处理
        },
        onNavigateToRegister = {
            // TODO: 注册导航处理  
        }
    )
}