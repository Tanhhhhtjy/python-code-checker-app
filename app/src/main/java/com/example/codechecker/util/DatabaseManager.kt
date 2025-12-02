package com.example.codechecker.util

import android.content.Context
import com.example.codechecker.data.local.database.AppDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

object DatabaseManager {
    
    private lateinit var database: AppDatabase
    
    fun initialize(context: Context) {
        database = AppDatabase.getInstance(context)
        // 可选：添加测试数据
        CoroutineScope(Dispatchers.IO).launch {
            addTestData()
        }
    }
    
    fun getUserDao() = database.userDao()
    fun getCodeFileDao() = database.codeFileDao()
    
    private suspend fun addTestData() {
        val userDao = database.userDao()
        // 检查是否已有测试用户
        val testUsers = listOf("student1", "teacher1")
        val existingUsers = testUsers.sumOf { username ->
            userDao.checkUsernameExists(username)
        }
        
        if (existingUsers == 0) {
            // 添加测试学生
            userDao.insert(
                com.example.codechecker.data.local.entity.UserEntity(
                    username = "student1",
                    passwordHash = HashUtil().hashPassword("123456"),
                    displayName = "张三",
                    role = "STUDENT"
                )
            )
            
            // 添加测试教师
            userDao.insert(
                com.example.codechecker.data.local.entity.UserEntity(
                    username = "teacher1",
                    passwordHash = HashUtil().hashPassword("123456"),
                    displayName = "张老师",
                    role = "TEACHER"
                )
            )
        }
    }
}