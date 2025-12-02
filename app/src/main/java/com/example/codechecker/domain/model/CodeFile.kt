package com.example.codechecker.domain.model

import java.util.Date

data class CodeFile(
    val id: Long = 0,
    val userId: Long,
    val fileName: String,
    val displayName: String,
    val codeContent: String,
    val language: String = "python",
    val fileSize: Long,
    val lineCount: Int,
    val uploadedAt: Date,
    val lastModifiedAt: Date = Date(),
    val isDeleted: Boolean = false
) {
    
    // 计算文件大小（友好显示）
    val formattedSize: String
        get() = when {
            fileSize < 1024 -> "$fileSize B"
            fileSize < 1024 * 1024 -> "${fileSize / 1024} KB"
            else -> "${fileSize / (1024 * 1024)} MB"
        }
    
    // 获取文件扩展名
    val fileExtension: String
        get() = fileName.substringAfterLast('.', "")
    
    // 检查是否为Python文件
    val isPythonFile: Boolean
        get() = fileExtension.equals("py", ignoreCase = true)
}