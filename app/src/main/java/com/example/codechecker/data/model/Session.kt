package com.example.codechecker.data.model

data class Session(
    val userId: Long,
    val username: String,
    val displayName: String,
    val role: String, // "STUDENT" 或 "TEACHER"
    val isLoggedIn: Boolean = true
)