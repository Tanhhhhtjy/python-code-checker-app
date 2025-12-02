package com.example.codechecker.ui.screens.code

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.codechecker.data.manager.SessionManager
import com.example.codechecker.data.repository.CodeFileRepositoryImpl
import com.example.codechecker.util.DatabaseManager

class FileViewModelFactory(
    private val sessionManager: SessionManager
) : ViewModelProvider.Factory {
    
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(FileViewModel::class.java)) {
            // 获取当前用户ID
            val userId = runBlocking {
                sessionManager.getUserId() ?: 0L
            }
            
            // 创建Repository
            val repository = CodeFileRepositoryImpl(
                DatabaseManager.getCodeFileDao()
            )
            
            return FileViewModel(repository, userId) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

// 辅助函数：在协程中获取用户ID
private fun runBlocking(block: suspend () -> Long): Long {
    return kotlinx.coroutines.runBlocking {
        block()
    }
}