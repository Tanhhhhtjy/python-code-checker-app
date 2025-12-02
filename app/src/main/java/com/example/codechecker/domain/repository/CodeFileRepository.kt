package com.example.codechecker.domain.repository

import com.example.codechecker.domain.model.CodeFile
import kotlinx.coroutines.flow.Flow

interface CodeFileRepository {
    
    // 保存代码文件
    suspend fun saveCodeFile(
        userId: Long,
        fileName: String,
        codeContent: String,
        displayName: String? = null
    ): Result<Long>
    
    // 获取用户的所有文件
    fun getFilesByUser(userId: Long): Flow<List<CodeFile>>
    
    // 获取单个文件
    suspend fun getFileById(fileId: Long): CodeFile?
    
    // 更新文件显示名称
    suspend fun updateDisplayName(fileId: Long, displayName: String): Result<Unit>
    
    // 删除文件
    suspend fun deleteFile(fileId: Long): Result<Unit>
    
    // 获取文件数量
    suspend fun getFileCount(userId: Long): Int
    
    // 搜索文件
    suspend fun searchFiles(userId: Long, query: String): List<CodeFile>
}