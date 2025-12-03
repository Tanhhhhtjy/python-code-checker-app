package com.example.codechecker.data.local.dao

import androidx.room.*
import com.example.codechecker.data.local.entity.UserEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    
    // 插入操作
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(user: UserEntity)
    
    // 查询操作
    @Query("SELECT * FROM users WHERE id = :userId")
    suspend fun getUserById(userId: Long): UserEntity?
    
    @Query("SELECT * FROM users WHERE username = :username")
    suspend fun getUserByUsername(username: String): UserEntity?

    @Query("SELECT * FROM users WHERE is_current = 1 LIMIT 1")
    fun getCurrentUserStream(): Flow<UserEntity?>
    
    // 存在性检查
    @Query("SELECT COUNT(*) FROM users WHERE username = :username")
    suspend fun checkUsernameExists(username: String): Int
    
    // 更新操作
    @Update
    suspend fun updateUser(user: UserEntity)
    
    @Query("UPDATE users SET last_login_at = :timestamp WHERE id = :userId")
    suspend fun updateLastLogin(userId: Long, timestamp: Long)
    
    // 批量查询（用于教师查看学生列表）
    @Query("SELECT * FROM users WHERE role = 'STUDENT' ORDER BY display_name ASC")
    fun getAllStudents(): Flow<List<UserEntity>>
    
    @Query("SELECT * FROM users WHERE role = 'TEACHER' ORDER BY display_name ASC")
    fun getAllTeachers(): Flow<List<UserEntity>>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(user: UserEntity)
    
    @Query("UPDATE users SET is_current = 0")
    suspend fun clearCurrentUser()
    
    @Query("UPDATE users SET is_current = 1 WHERE id = :userId")
    suspend fun setCurrentUser(userId: Long)
    
    // 删除操作（通常不使用，仅用于测试）
    @Delete
    suspend fun deleteUser(user: UserEntity)
    
    @Query("DELETE FROM users")
    suspend fun deleteAllUsers()
}