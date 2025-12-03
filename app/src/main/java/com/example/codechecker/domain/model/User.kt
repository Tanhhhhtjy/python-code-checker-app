package com.example.codechecker.domain.model

import java.util.Date

data class User(
    val id: Long = 0,
    val username: String,
    val displayName: String,
    val passwordHash: String,
    val role: UserRole,
    val createdAt: Long,
    val lastLoginAt: Date? = null
)

enum class UserRole {
    STUDENT, TEACHER
}

// 登录状态
sealed class AuthState {
    object Loading : AuthState()
    data class Authenticated(val user: User) : AuthState()
    object Unauthenticated : AuthState()
    data class Error(val message: String) : AuthState()
}