package com.example.codechecker.ui.screens.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import com.example.codechecker.domain.model.User
import com.example.codechecker.domain.model.UserRole
import com.example.codechecker.domain.model.AuthState
import com.example.codechecker.util.HashUtil

class AuthViewModel : ViewModel() {
    
    // 模拟用户数据 - 实际项目中应该从数据库获取
    private val mockUsers = listOf(
        User(
            id = 1,
            username = "student1",
            displayName = "张三",
            role = UserRole.STUDENT,
            createdAt = System.currentTimeMillis()
        ),
        User(
            id = 2,
            username = "teacher1",
            displayName = "李老师",
            role = UserRole.TEACHER,
            createdAt = System.currentTimeMillis()
        )
    )
    
    private val _authState = MutableStateFlow<AuthState>(AuthState.Unauthenticated)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()
    
    private val _loginState = MutableStateFlow(LoginState())
    val loginState: StateFlow<LoginState> = _loginState.asStateFlow()
    
    private val _registerState = MutableStateFlow(RegisterState())
    val registerState: StateFlow<RegisterState> = _registerState.asStateFlow()
    
    fun login(username: String, password: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            _loginState.value = _loginState.value.copy(isLoading = true)
            
            // 模拟网络请求延迟
            kotlinx.coroutines.delay(1000)
            
            // 简单的认证逻辑 - 实际项目中应该查询数据库
            val user = mockUsers.find { it.username == username }
            
            if (user != null && password == "123456") { // 简单密码验证
                _authState.value = AuthState.Authenticated(user)
                _loginState.value = LoginState(
                    isLoading = false,
                    errorMessage = null
                )
            } else {
                _authState.value = AuthState.Error("用户名或密码错误")
                _loginState.value = _loginState.value.copy(
                    isLoading = false,
                    errorMessage = "用户名或密码错误"
                )
            }
        }
    }
    
    fun register(username: String, password: String, displayName: String, role: UserRole) {
        viewModelScope.launch {
            _registerState.value = _registerState.value.copy(isLoading = true)
            
            // 模拟网络请求延迟
            kotlinx.coroutines.delay(1000)
            
            // 检查用户名是否已存在
            if (mockUsers.any { it.username == username }) {
                _registerState.value = _registerState.value.copy(
                    isLoading = false,
                    errorMessage = "用户名已存在"
                )
                return@launch
            }
            
            // 注册成功
            _registerState.value = _registerState.value.copy(
                isLoading = false,
                isSuccess = true,
                errorMessage = null
            )
        }
    }
    
    fun logout() {
        _authState.value = AuthState.Unauthenticated
        clearLoginForm()
        clearRegisterForm()
    }
    
    fun updateLoginUsername(username: String) {
        _loginState.value = _loginState.value.copy(username = username)
    }
    
    fun updateLoginPassword(password: String) {
        _loginState.value = _loginState.value.copy(password = password)
    }
    
    fun updateRegisterUsername(username: String) {
        _registerState.value = _registerState.value.copy(username = username)
    }
    
    fun updateRegisterPassword(password: String) {
        _registerState.value = _registerState.value.copy(password = password)
    }
    
    fun updateRegisterDisplayName(displayName: String) {
        _registerState.value = _registerState.value.copy(displayName = displayName)
    }
    
    fun updateRegisterRole(role: UserRole) {
        _registerState.value = _registerState.value.copy(role = role)
    }
    
    fun clearLoginForm() {
        _loginState.value = LoginState()
    }
    
    fun clearRegisterForm() {
        _registerState.value = RegisterState()
    }
    
    fun clearError() {
        _loginState.value = _loginState.value.copy(errorMessage = null)
        _registerState.value = _registerState.value.copy(errorMessage = null)
    }
}

data class LoginState(
    val username: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

data class RegisterState(
    val username: String = "",
    val password: String = "",
    val displayName: String = "",
    val role: UserRole = UserRole.STUDENT,
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val errorMessage: String? = null
)