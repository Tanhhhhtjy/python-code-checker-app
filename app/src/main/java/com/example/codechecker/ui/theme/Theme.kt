package com.example.codechecker.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable

@Composable
fun CodeCheckerTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colors = if (darkTheme) {
        DarkColorScheme // 使用 Color.kt 中的定义
    } else {
        LightColorScheme // 使用 Color.kt 中的定义
    }

    MaterialTheme(
        colorScheme = colors, // 修复参数名称
        typography = Typography,
        content = content
    )
}