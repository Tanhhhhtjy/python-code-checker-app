package com.example.codechecker.data.repository

import com.example.codechecker.data.local.dao.UserDao
import com.example.codechecker.data.local.entity.UserEntity
import com.example.codechecker.data.mapper.toDomain
import com.example.codechecker.data.mapper.toEntity
import com.example.codechecker.data.repository.UserRepository
import com.example.codechecker.data.manager.SessionManager
import com.example.codechecker.domain.model.User
import com.example.codechecker.domain.model.UserRole
import com.example.codechecker.CodeCheckerApp
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepositoryImpl @Inject constructor(
    private val userDao: UserDao,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : UserRepository {
    
    private var currentUserId: Long? = null
    private val sessionManager = CodeCheckerApp.sessionManager

    override suspend fun registerUser(
        username: String,
        password: String,
        displayName: String,
        role: UserRole
    ): Result<Long> {
        return withContext(ioDispatcher) {
            try {
                // 1. 检查用户名是否已存在
                val existingUser = userDao.getUserByUsername(username)
                if (existingUser != null) {
                    return@withContext Result.failure(Exception("用户名已存在"))
                }
                
                // 2. 创建新用户（模拟生成ID）
                val newId = System.currentTimeMillis() // 临时用时间戳作为ID
                val user = User(
                    id = newId,
                    username = username,
                    passwordHash = password, // 简化处理，实际应存储哈希值
                    displayName = displayName,
                    role = role,
                    createdAt = System.currentTimeMillis()
                )
                
                // 3. 保存到数据库
                userDao.insert(user.toEntity())
                
                Result.success(newId)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    override suspend fun loginUser(username: String, password: String): Result<User> {
        return withContext(ioDispatcher) {
            try {
                // 1. 从数据库查找用户
                val userEntity = userDao.getUserByUsername(username)
                
                if (userEntity == null) {
                    return@withContext Result.failure(Exception("用户不存在"))
                }
                
                // 2. 简单的密码验证（实际应该用Hash验证）
                // 这里为了简单，假设所有用户密码都是"123456"
                if (password != "123456") {
                    return@withContext Result.failure(Exception("密码错误"))
                }
                
                // 3. 返回用户
                Result.success(userEntity.toDomain())
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
    
    override suspend fun getUserById(userId: Long): User? {
        return withContext(ioDispatcher) {
            // 先从本地获取
            val cachedUser = userDao.getUserById(userId)
            cachedUser?.toDomain()
        }
    }
    
    override fun getCurrentUserStream(): Flow<User?> {
        return userDao.getCurrentUserStream()
            .map { entity ->
                entity?.toDomain()
            }
    }
    
    override suspend fun updateUser(
        displayName: String?
    ): Result<User> {
        return Result.failure(Exception("未实现"))
    }
    
    override suspend fun changePassword(
        oldPassword: String, 
        newPassword: String
    ): Result<Unit> {
        return Result.failure(Exception("未实现"))
    }
    
    override suspend fun deleteAccount(): Result<Unit> {
        return Result.failure(Exception("未实现"))
    }
    
    override suspend fun getStudents(): Result<List<User>> {
        return Result.failure(Exception("未实现"))
    }
    
    override suspend fun getStudentDetail(studentId: Long): Result<User> {
        return Result.failure(Exception("未实现"))
    }

    override suspend fun logout() {
        withContext(ioDispatcher) {
            clearCache()
            // 清除session
            sessionManager.clearSession()
        }
    }
    
    override suspend fun clearCache() {
        withContext(ioDispatcher) {
            userDao.clearCurrentUser()
        }
    }
}