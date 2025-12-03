package com.example.codechecker.data.model

import com.example.codechecker.domain.model.UserRole

data class RegisterRequest(
    val username: String,
    val password: String,
    val displayName: String,
    val role: UserRole
)

data class RegisterResponse(
    val userId: Long,
    val message: String
)