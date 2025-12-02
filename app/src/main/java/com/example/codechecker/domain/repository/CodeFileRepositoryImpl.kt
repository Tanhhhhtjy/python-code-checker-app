// data/repository/CodeFileRepositoryImpl.kt
package com.example.codechecker.data.repository

import com.example.codechecker.data.local.dao.CodeFileDao
import com.example.codechecker.data.local.entity.CodeFileEntity
import com.example.codechecker.domain.model.CodeFile
import com.example.codechecker.domain.repository.CodeFileRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import java.util.Date

class CodeFileRepositoryImpl(
    private val codeFileDao: CodeFileDao
) : CodeFileRepository {
    
    override suspend fun saveCodeFile(
        userId: Long,
        fileName: String,
        codeContent: String,
        displayName: String?
    ): Result<Long> = withContext(Dispatchers.IO) {
        try {
            // 验证代码内容
            if (codeContent.isBlank()) {
                return@withContext Result.failure(IllegalArgumentException("代码内容不能为空"))
            }
            
            // 计算文件信息
            val lines = codeContent.lines()
            val lineCount = lines.size
            val fileSize = codeContent.toByteArray().size.toLong()
            
            // 创建实体
            val entity = CodeFileEntity(
                userId = userId,
                fileName = fileName,
                displayName = displayName ?: fileName,
                codeContent = codeContent,
                fileSize = fileSize,
                lineCount = lineCount,
                uploadedAt = Date()
            )
            
            // 保存到数据库
            val fileId = codeFileDao.insertFile(entity)
            Result.success(fileId)
            
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override fun getFilesByUser(userId: Long): Flow<List<CodeFile>> {
        return codeFileDao.getFilesByUser(userId)
            .map { entities ->
                entities.map { it.toDomain() }
            }
    }
    
    override suspend fun getFileById(fileId: Long): CodeFile? = withContext(Dispatchers.IO) {
        codeFileDao.getFileById(fileId)?.toDomain()
    }
    
    override suspend fun updateDisplayName(fileId: Long, displayName: String): Result<Unit> = 
        withContext(Dispatchers.IO) {
        try {
            val entity = codeFileDao.getFileById(fileId)
                ?: return@withContext Result.failure(Exception("文件不存在"))
            
            val updatedEntity = entity.copy(
                displayName = displayName,
                lastModifiedAt = Date()
            )
            
            codeFileDao.updateFile(updatedEntity)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun deleteFile(fileId: Long): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            codeFileDao.softDeleteFile(fileId)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun getFileCount(userId: Long): Int = withContext(Dispatchers.IO) {
        codeFileDao.getFileCountByUser(userId)
    }
    
    override suspend fun searchFiles(userId: Long, query: String): List<CodeFile> = 
        withContext(Dispatchers.IO) {
        codeFileDao.searchFiles(userId, query).map { it.toDomain() }
    }
    
    // 扩展函数：实体转领域模型
    private fun CodeFileEntity.toDomain(): CodeFile {
        return CodeFile(
            id = this.id,
            userId = this.userId,
            fileName = this.fileName,
            displayName = this.displayName,
            codeContent = this.codeContent,
            language = this.language,
            fileSize = this.fileSize,
            lineCount = this.lineCount,
            uploadedAt = this.uploadedAt,
            lastModifiedAt = this.lastModifiedAt,
            isDeleted = this.isDeleted
        )
    }
}