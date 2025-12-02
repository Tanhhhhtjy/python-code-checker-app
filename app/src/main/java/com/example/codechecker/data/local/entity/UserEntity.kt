package com.example.codechecker.data.local.entity

import androidx.room.*
import java.util.Date

@Entity(
    tableName = "users",
    indices = [
        Index(value = ["username"], unique = true)
    ]
)
data class UserEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    
    @ColumnInfo(name = "username")
    val username: String,
    
    @ColumnInfo(name = "password_hash")
    val passwordHash: String,
    
    @ColumnInfo(name = "display_name")
    val displayName: String,
    
    @ColumnInfo(name = "role")
    val role: String, // "STUDENT" 或 "TEACHER"
    
    @ColumnInfo(name = "created_at")
    val createdAt: Date = Date(),
    
    @ColumnInfo(name = "last_login_at")
    val lastLoginAt: Date? = null,
    
    @ColumnInfo(name = "is_active")
    val isActive: Boolean = true
) {
    companion object {
        fun createStudent(
            username: String,
            passwordHash: String,
            displayName: String
        ): UserEntity {
            return UserEntity(
                username = username,
                passwordHash = passwordHash,
                displayName = displayName,
                role = "STUDENT"
            )
        }
        
        fun createTeacher(
            username: String,
            passwordHash: String,
            displayName: String
        ): UserEntity {
            return UserEntity(
                username = username,
                passwordHash = passwordHash,
                displayName = displayName,
                role = "TEACHER"
            )
        }
    }
}