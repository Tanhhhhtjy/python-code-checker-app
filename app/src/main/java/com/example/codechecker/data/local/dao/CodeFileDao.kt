package com.example.codechecker.data.local.dao

import androidx.room.*
import com.example.codechecker.data.local.entity.CodeFileEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CodeFileDao {
    
    // 插入文件
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFile(file: CodeFileEntity): Long
    
    // 更新文件
    @Update
    suspend fun updateFile(file: CodeFileEntity)
    
    // 软删除文件
    @Query("UPDATE code_files SET is_deleted = 1 WHERE id = :fileId")
    suspend fun softDeleteFile(fileId: Long)
    
    // 获取单个文件
    @Query("SELECT * FROM code_files WHERE id = :fileId AND is_deleted = 0")
    suspend fun getFileById(fileId: Long): CodeFileEntity?
    
    // 获取用户的所有文件
    @Query("SELECT * FROM code_files WHERE user_id = :userId AND is_deleted = 0 ORDER BY uploaded_at DESC")
    fun getFilesByUser(userId: Long): Flow<List<CodeFileEntity>>
    
    // 获取文件数量
    @Query("SELECT COUNT(*) FROM code_files WHERE user_id = :userId AND is_deleted = 0")
    suspend fun getFileCountByUser(userId: Long): Int
    
    // 搜索文件
    @Query("SELECT * FROM code_files WHERE user_id = :userId AND (file_name LIKE '%' || :query || '%' OR display_name LIKE '%' || :query || '%') AND is_deleted = 0")
    suspend fun searchFiles(userId: Long, query: String): List<CodeFileEntity>

    // 添加：检查用户是否有同名文件
    @Query("SELECT COUNT(*) FROM code_files WHERE user_id = :userId AND file_name = :fileName AND is_deleted = 0")
    suspend fun countFilesByName(userId: Long, fileName: String): Int
    
    // 添加：根据文件名模式搜索文件
    @Query("SELECT * FROM code_files WHERE user_id = :userId AND file_name LIKE :pattern AND is_deleted = 0")
    suspend fun findFilesByPattern(userId: Long, pattern: String): List<CodeFileEntity>
    
    // 清理已删除的文件（可选）
    @Query("DELETE FROM code_files WHERE is_deleted = 1")
    suspend fun cleanupDeletedFiles()
}