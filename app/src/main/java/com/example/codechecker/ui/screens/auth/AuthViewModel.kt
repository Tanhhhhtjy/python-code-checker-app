package com.example.codechecker.ui.screens.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import com.example.codechecker.domain.model.User
import com.example.codechecker.domain.model.UserRole
import com.example.codechecker.domain.model.AuthState
import com.example.codechecker.util.HashUtil
import com.example.codechecker.CodeCheckerApp
import com.example.codechecker.data.manager.SessionManager

class AuthViewModel : ViewModel() {
    
    // 添加SessionManager
    private val sessionManager = CodeCheckerApp.sessionManager

    // 模拟用户数据 - 实际项目中应该从数据库获取
    private val _mockUsers = mutableListOf(
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

    val mockUsers: List<User> = _mockUsers
    
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
            val user = _mockUsers.find { it.username == username }
            
            if (user != null && password == "123456") { // 简单密码验证
                // 保存用户会话到DataStore
                sessionManager.saveSession(
                    userId = user.id,
                    username = user.username,
                    displayName = user.displayName,
                    role = user.role.name
                )

                _authState.value = AuthState.Authenticated(user)
                _loginState.value = LoginState(
                    isLoading = false,
                    errorMessage = null,
                    loginSuccess = true
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
            if (_mockUsers.any { it.username == username }) {
                _registerState.value = _registerState.value.copy(
                    isLoading = false,
                    errorMessage = "用户名已存在"
                )
                return@launch
            }

            // 模拟创建用户
            val newUser = User(
                id = (mockUsers.size + 1).toLong(),
                username = username,
                displayName = displayName,
                role = role,
                createdAt = System.currentTimeMillis()
            )

            // 保存用户会话
            sessionManager.saveSession(
                userId = newUser.id,
                username = newUser.username,
                displayName = newUser.displayName,
                role = newUser.role.name
            )

            // 注册成功 - 延迟一下确保DataStore保存完成
            kotlinx.coroutines.delay(300)
            
            // 注册成功
            _registerState.value = _registerState.value.copy(
                isLoading = false,
                isSuccess = true,
                errorMessage = null
            )

            // 设置认证状态
            _authState.value = AuthState.Authenticated(newUser)
        }
    }
    
    // 登出方法
    fun logout() {
        viewModelScope.launch {
            sessionManager.clearSession()
            _authState.value = AuthState.Unauthenticated
            clearLoginForm()
            clearRegisterForm()
        }
    }

     // 新增：检查登录状态
     fun checkLoginStatus() {
        viewModelScope.launch {
            val isLoggedIn = sessionManager.isLoggedInFlow.firstOrNull() ?: false
            if (isLoggedIn) {
                // 如果已登录，可以获取用户信息并更新状态
                // 这里暂时只更新状态为已登录
                _authState.value = AuthState.Authenticated(
                    User(
                        id = 0,
                        username = "已登录用户",
                        displayName = "用户",
                        role = UserRole.STUDENT,
                        createdAt = System.currentTimeMillis()
                    )
                )
            }
        }
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
        //println("=== 角色更新调试 ===")
        //println("更新前角色: ${_registerState.value.role}")
        _registerState.value = _registerState.value.copy(role = role)
        //println("更新后角色: ${_registerState.value.role}")
        //println("=================")
    }
    
    fun clearLoginForm() {
        _loginState.value = LoginState()
    }
    
    fun clearRegisterForm() {
        _registerState.value = RegisterState()
    }

    // 更新注册错误消息
    fun updateRegisterError(message: String) {
        _registerState.value = _registerState.value.copy(errorMessage = message)
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
    val errorMessage: String? = null,
    val loginSuccess: Boolean = false
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