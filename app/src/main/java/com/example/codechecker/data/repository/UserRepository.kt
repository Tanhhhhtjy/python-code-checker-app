// domain/repository/UserRepository.kt
package com.example.codechecker.domain.repository

import com.example.codechecker.domain.model.User
import com.example.codechecker.domain.model.UserRole

interface UserRepository {
    suspend fun registerUser(
        username: String,
        password: String,
        displayName: String,
        role: UserRole
    ): Result<Long>
    
    suspend fun loginUser(username: String, password: String): Result<User>
    
    suspend fun getUserById(userId: Long): User?
    
    suspend fun logout()
}