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
import com.example.codechecker.data.repository.UserRepository
import javax.inject.Inject

class AuthViewModel @Inject constructor(
    private val userRepository: UserRepository  
) : ViewModel() {
    
    // 添加SessionManager
    private val sessionManager = CodeCheckerApp.sessionManager
    
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

            val result = userRepository.loginUser(username, password)
            
            result.onSuccess { user ->
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
            }.onFailure { exception ->
                _authState.value = AuthState.Error(exception.message ?: "登录失败")
                _loginState.value = _loginState.value.copy(
                    isLoading = false,
                    errorMessage = exception.message ?: "登录失败"
                )
            }
        }
    }
    
    fun register(username: String, password: String, displayName: String, role: UserRole) {
        viewModelScope.launch {
            _registerState.value = _registerState.value.copy(isLoading = true)

            val result = userRepository.registerUser(
                username = username,
                password = password,
                displayName = displayName,
                role = role
            )
            
            result.onSuccess { userId ->
                // 注册成功后，模拟注册的用户自动登录
                val loginResult = userRepository.loginUser(username, password) 
                
                loginResult.onSuccess { user ->
                    // 保存会话
                    sessionManager.saveSession(
                        userId = user.id,
                        username = user.username,
                        displayName = user.displayName,
                        role = user.role.name
                    )
                    
                    _registerState.value = _registerState.value.copy(
                        isLoading = false,
                        isSuccess = true,
                        errorMessage = null
                    )
                    _authState.value = AuthState.Authenticated(user)
                    
                }.onFailure { loginException ->
                    _registerState.value = _registerState.value.copy(
                        isLoading = false,
                        errorMessage = "注册成功但自动登录失败: ${loginException.message}"
                    )
                }
                
            }.onFailure { exception ->
                _registerState.value = _registerState.value.copy(
                    isLoading = false,
                    errorMessage = exception.message ?: "注册失败"
                )
            }
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

     // 检查登录状态
     fun checkLoginStatus() {
        viewModelScope.launch {
            try {
                // 检查是否登录
                val isLoggedIn = sessionManager.isLoggedInFlow.firstOrNull() ?: false
                
                if (isLoggedIn) {
                    val currentSession = sessionManager.getCurrentSession() 
                    
                    currentSession?.let { session ->
                        val user = userRepository.getUserById(session.userId)
                        
                        user?.let { foundUser ->
                            _authState.value = AuthState.Authenticated(foundUser)
                        } ?: run {
                            _authState.value = AuthState.Unauthenticated
                        }
                    } ?: run {
                        _authState.value = AuthState.Unauthenticated
                    }
                } else {
                    _authState.value = AuthState.Unauthenticated
                }
            } catch (e: Exception) {
                _authState.value = AuthState.Error("检查登录状态时出错: ${e.message}")
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