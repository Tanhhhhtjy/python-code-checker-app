package com.example.codechecker.ui.screens.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.codechecker.CodeCheckerApp
import com.example.codechecker.data.model.Session
import com.example.codechecker.data.manager.SessionManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MainViewModel(
    private val sessionManager: SessionManager
) : ViewModel() {
    
    private val _appState = MutableStateFlow(AppState())
    val appState: StateFlow<AppState> = _appState.asStateFlow()
    
    init {
        // 应用启动时检查登录状态
        checkLoginStatus()
    }
    
    private fun checkLoginStatus() {
        viewModelScope.launch {
            sessionManager.isLoggedInFlow.collect { isLoggedIn ->
                _appState.value = _appState.value.copy(
                    isLoggedIn = isLoggedIn,
                    isLoading = false
                )
            }
        }
    }
    
    // 获取当前会话信息
    fun getCurrentSession() {
        viewModelScope.launch {
            sessionManager.sessionFlow.collect { session ->
                _appState.value = _appState.value.copy(
                    currentSession = session,
                    isLoading = false
                )
            }
        }
    }
    
    // 登出
    fun logout() {
        viewModelScope.launch {
            sessionManager.clearSession()
            _appState.value = AppState(
                isLoggedIn = false,
                currentSession = null,
                isLoading = false
            )
        }
    }
}

data class AppState(
    val isLoggedIn: Boolean = false,
    val currentSession: com.example.codechecker.data.model.Session? = null,
    val isLoading: Boolean = true
)