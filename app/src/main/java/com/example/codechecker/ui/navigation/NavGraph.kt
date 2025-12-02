package com.example.codechecker.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.navigation.NavType
import com.example.codechecker.ui.screens.auth.LoginScreen
import com.example.codechecker.ui.screens.auth.RegisterScreen
import com.example.codechecker.ui.screens.code.FileListScreen
import com.example.codechecker.ui.screens.code.CodeUploadScreen
import com.example.codechecker.ui.screens.code.FileViewModelFactory
import com.example.codechecker.ui.screens.code.FileDetailScreen
import com.example.codechecker.ui.screens.main.HomeScreen
import com.example.codechecker.ui.screens.main.MainViewModel
import com.example.codechecker.CodeCheckerApp

@Composable
fun NavGraph() {
    val navController = rememberNavController()
    val appState = MainViewModel(CodeCheckerApp.sessionManager).appState.value

    // 手动创建MainViewModel实例
    val mainViewModel = remember {
        MainViewModel(CodeCheckerApp.sessionManager)
    }

    // 创建FileViewModel工厂
    val fileViewModelFactory = remember {
        FileViewModelFactory(CodeCheckerApp.sessionManager)
    }
    
    // 使用remember保存起始页面
    val startDestination = remember { 
        // 这里需要同步获取初始状态，但由于Flow是异步的，我们先默认登录页
        // 实际登录状态会在LaunchedEffect中处理
        "login"
    }

    LaunchedEffect(appState.isLoggedIn) {
        if (appState.isLoggedIn) {
            navController.navigate("home") {
                popUpTo("login") { inclusive = true }
            }
        }
    }
    
    NavHost(
        navController = navController,
        startDestination = "login"
    ) {
        composable("login") {
            LoginScreen(
                onLoginSuccess = {
                    // 登录成功后跳转到首页
                    navController.navigate("home") {
                        popUpTo("login") { inclusive = true }
                    }
                },
                onNavigateToRegister = {
                    navController.navigate("register")
                }
            )
        }
        
        composable("register") {
            RegisterScreen(
                onRegisterSuccess = {
                    // 注册成功后跳转到登录页
                    navController.navigate("login") {
                        popUpTo("register") { inclusive = true }
                    }
                },
                onNavigateToLogin = {
                    navController.navigate("login") {
                        popUpTo("register") { inclusive = true }
                    }
                }
            )
        }
        
        // 首页
        composable("home") {
            HomeScreen(
                onLogout = {
                    mainViewModel.logout()
                    navController.navigate("login") {
                        popUpTo("home") { inclusive = true }
                    }
                },
                onNavigateToFileList = {
                    navController.navigate("file_list")
                },
                onNavigateToUpload = {
                    navController.navigate("code_upload")
                },
                onNavigateToPlagiarism = {
                    navController.navigate("plagiarism_home")
                },
                onNavigateToProfile = {
                    navController.navigate("profile")
                },
                mainViewModel = mainViewModel
            )
        }

        composable("file_list") {
            FileListScreen(
                onFileClick = { fileId ->
                    // 跳转到文件详情（后续实现）
                    navController.navigate("file_detail/$fileId")
                },
                onUploadClick = {
                    navController.navigate("code_upload")
                },
                onNavigateToHome = {
                    navController.navigate("home") {
                        popUpTo("home") { inclusive = false }
                    }
                },
                viewModelFactory = fileViewModelFactory
            )
        }

        composable("code_upload") {
            CodeUploadScreen(
                onUploadSuccess = {
                    navController.navigate("file_list") {
                        popUpTo("file_list") { inclusive = false }
                    }
                },
                onNavigateToHome = {
                    navController.navigate("home") {
                        popUpTo("home") { inclusive = false }
                    }
                },
                viewModelFactory = fileViewModelFactory
            )
        }

        // 文件详情页面
        composable(
            route = "file_detail/{fileId}",
            arguments = listOf(
                navArgument("fileId") { type = NavType.LongType }
            )
        ) { backStackEntry ->
            val fileId = backStackEntry.arguments?.getLong("fileId") ?: 0L
            FileDetailScreen(
                fileId = fileId,
                onNavigateBack = { navController.popBackStack() },
                onNavigateToPlagiarism = { fileId ->
                    // 导航到查重页面
                    navController.navigate("plagiarism_detail/$fileId")
                },
                mainViewModel = mainViewModel, // 传递mainViewModel以获取用户身份
                viewModelFactory = fileViewModelFactory
            )
        }

        // 个人中心页面（稍后实现）
        composable("profile") {
            // 个人中心页面
            androidx.compose.material3.Text("个人中心 - 开发中")
        }

        // 查重首页（稍后实现）
        composable("plagiarism_home") {
            // 查重功能首页
            androidx.compose.material3.Text("代码查重 - 开发中")
        }
    }
}