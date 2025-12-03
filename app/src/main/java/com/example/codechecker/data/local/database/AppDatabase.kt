package com.example.codechecker.data.local.database

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import android.content.Context
import com.example.codechecker.data.local.dao.UserDao
import com.example.codechecker.data.local.dao.CodeFileDao
import com.example.codechecker.data.local.entity.UserEntity
import com.example.codechecker.data.local.entity.CodeFileEntity

@Database(
    entities = [
        UserEntity::class,
        CodeFileEntity::class
    ],
    version = 3,
    exportSchema = false
)
@TypeConverters(DateConverters::class)
abstract class AppDatabase : RoomDatabase() {
    
    abstract fun userDao(): UserDao
    abstract fun codeFileDao(): CodeFileDao
    
    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null
        
        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "codechecker_database"
                )
                .fallbackToDestructiveMigration() // 开发阶段使用，正式版需要实现迁移
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}