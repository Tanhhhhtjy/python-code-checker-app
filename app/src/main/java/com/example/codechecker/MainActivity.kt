package com.example.codechecker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.example.codechecker.ui.navigation.NavGraph
import com.example.codechecker.ui.theme.CodeCheckerTheme
import dagger.hilt.android.AndroidEntryPoint

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CodeCheckerTheme {
                Surface(
                    modifier = Modifier.fillMaxSize()
                ) {
                    NavGraph() //添加导航
                }
            }
        }
    }
}