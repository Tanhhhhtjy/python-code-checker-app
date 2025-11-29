package com.example.codechecker.data.local.entity

// 简化版本，先不使用Room
data class UserEntity(
    val id: Long = 0,
    val username: String,
    val passwordHash: String,
    val displayName: String,
    val role: String,
    val createdAt: Long = System.currentTimeMillis()
)