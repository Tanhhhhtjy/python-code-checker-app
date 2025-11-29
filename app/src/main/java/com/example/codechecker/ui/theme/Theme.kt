package com.example.codechecker.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable

private val LightColorPalette = lightColors(
    primary = androidx.compose.ui.graphics.Color(0xFF006A6B),
    primaryVariant = androidx.compose.ui.graphics.Color(0xFF004F51),
    secondary = androidx.compose.ui.graphics.Color(0xFF4A6363)
)

private val DarkColorPalette = darkColors(
    primary = androidx.compose.ui.graphics.Color(0xFF4CDADB),
    primaryVariant = androidx.compose.ui.graphics.Color(0xFF004F51),
    secondary = androidx.compose.ui.graphics.Color(0xFFB1CCCB)
)

@Composable
fun CodeCheckerTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colors = if (darkTheme) {
        DarkColorPalette
    } else {
        LightColorPalette
    }

    MaterialTheme(
        colors = colors,
        typography = Typography,
        shapes = androidx.compose.material.Shapes,
        content = content
    )
}