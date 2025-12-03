package com.example.codechecker.data.repository

import com.example.codechecker.domain.model.User
import com.example.codechecker.domain.model.UserRole
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    suspend fun registerUser(
        username: String,
        password: String,
        displayName: String,
        role: UserRole
    ): Result<Long>
    
    suspend fun loginUser(username: String, password: String): Result<User>
    
    suspend fun getUserById(userId: Long): User?

    fun getCurrentUserStream(): Flow<User?>

    suspend fun updateUser(displayName: String?): Result<User>

    suspend fun changePassword(oldPassword: String, newPassword: String): Result<Unit>

    suspend fun deleteAccount(): Result<Unit>

    suspend fun getStudents(): Result<List<User>>

    suspend fun getStudentDetail(studentId: Long): Result<User>
    
    suspend fun logout()

    suspend fun clearCache()
}