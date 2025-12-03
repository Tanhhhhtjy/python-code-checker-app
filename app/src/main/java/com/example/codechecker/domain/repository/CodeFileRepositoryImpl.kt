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

            // 生成唯一文件名（自动重命名逻辑）
            val uniqueFileName = generateUniqueFileName(userId, fileName)
            
            // 计算文件信息
            val lines = codeContent.lines()
            val lineCount = lines.size
            val fileSize = codeContent.toByteArray().size.toLong()
            
            // 创建实体
            val entity = CodeFileEntity(
                userId = userId,
                fileName = uniqueFileName,
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

    /**
     * 生成唯一文件名，避免用户文件重名
     * 规则：如果重名，添加序号：file(1).py, file(2).py
     */
    private suspend fun generateUniqueFileName(userId: Long, originalFileName: String): String {
        // 检查是否已有同名文件
        val count = codeFileDao.countFilesByName(userId, originalFileName)
        
        // 如果没有重名，直接返回原文件名
        if (count == 0) {
            return originalFileName
        }
        
        // 分离文件名和扩展名
        val (nameWithoutExt, extension) = splitFileName(originalFileName)
        val ext = if (extension.isNotEmpty()) ".$extension" else ""
        
        // 查找已有的带序号的文件
        val existingFiles = codeFileDao.findFilesByPattern(userId, "$nameWithoutExt(*)$ext")
        
        // 找出已使用的序号
        val usedNumbers = mutableSetOf<Int>()
        val pattern = Regex("""$nameWithoutExt\((\d+)\)$ext""")
        
        existingFiles.forEach { file ->
            val match = pattern.find(file.fileName)
            match?.groupValues?.get(1)?.toIntOrNull()?.let { usedNumbers.add(it) }
        }
        
        // 找到第一个可用的序号
        var number = 1
        while (usedNumbers.contains(number)) {
            number++
        }
        
        return "$nameWithoutExt($number)$ext"
    }

    // 分离文件名和扩展名
    private fun splitFileName(fileName: String): Pair<String, String> {
        val dotIndex = fileName.lastIndexOf('.')
        return if (dotIndex > 0) {
            val name = fileName.substring(0, dotIndex)
            val ext = fileName.substring(dotIndex + 1)
            Pair(name, ext)
        } else {
            Pair(fileName, "")
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