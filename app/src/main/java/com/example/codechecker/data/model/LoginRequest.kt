package com.example.codechecker.data.model

data class LoginRequest(
    val username: String,
    val password: String
)

data class LoginResponse(
    val user: com.example.codechecker.domain.model.User,
    val token: String
)