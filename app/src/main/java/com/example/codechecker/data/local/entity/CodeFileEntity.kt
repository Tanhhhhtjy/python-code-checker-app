package com.example.codechecker.data.local.entity

import androidx.room.*
import java.util.Date

@Entity(
    tableName = "code_files",
    indices = [
        Index(value = ["user_id"]),
        Index(value = ["file_name"]),
        Index(value = ["uploaded_at"])
    ]
)
data class CodeFileEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    
    @ColumnInfo(name = "user_id")
    val userId: Long,                     // 所属用户ID
    
    @ColumnInfo(name = "file_name")
    val fileName: String,                 // 原始文件名
    
    @ColumnInfo(name = "display_name")
    val displayName: String,              // 显示名称（可修改）
    
    @ColumnInfo(name = "code_content")
    val codeContent: String,              // 代码内容
    
    @ColumnInfo(name = "language")
    val language: String = "python",      // 编程语言（固定为python）
    
    @ColumnInfo(name = "file_size")
    val fileSize: Long,                   // 文件大小（字节）
    
    @ColumnInfo(name = "line_count")
    val lineCount: Int,                   // 代码行数
    
    @ColumnInfo(name = "uploaded_at")
    val uploadedAt: Date = Date(),        // 上传时间
    
    @ColumnInfo(name = "last_modified_at")
    val lastModifiedAt: Date = Date(),    // 最后修改时间
    
    @ColumnInfo(name = "is_deleted")
    val isDeleted: Boolean = false        // 软删除标记
)