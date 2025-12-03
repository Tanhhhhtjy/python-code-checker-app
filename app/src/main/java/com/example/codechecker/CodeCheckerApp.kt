package com.example.codechecker

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import com.example.codechecker.util.DatabaseManager
import com.example.codechecker.data.manager.SessionManager
import com.example.codechecker.data.local.database.AppDatabase
import com.example.codechecker.data.repository.UserRepositoryImpl
import com.example.codechecker.data.repository.UserRepository

class CodeCheckerApp : Application() {

    companion object {
        lateinit var sessionManager: SessionManager
            private set
        lateinit var instance: CodeCheckerApp
            private set
        lateinit var userRepository: UserRepository
            private set
    }
    
    override fun onCreate() {
        super.onCreate()
        instance = this
        // 初始化数据库
        DatabaseManager.initialize(this)

        // 初始化会话管理器
        sessionManager = SessionManager(this)

        // 初始化 UserRepository
        val database = AppDatabase.getInstance(this)
        userRepository = UserRepositoryImpl(database.userDao())
    }
}