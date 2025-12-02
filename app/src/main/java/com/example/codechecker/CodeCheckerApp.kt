package com.example.codechecker

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import com.example.codechecker.util.DatabaseManager
import com.example.codechecker.data.manager.SessionManager

@HiltAndroidApp
class CodeCheckerApp : Application() {

    companion object {
        lateinit var sessionManager: SessionManager
            private set
    }
    
    override fun onCreate() {
        super.onCreate()
        // 初始化数据库
        DatabaseManager.initialize(this)

        // 初始化会话管理器
        sessionManager = SessionManager(this)
    }
}