// ui/screens/code/FileViewModel.kt
package com.example.codechecker.ui.screens.code

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.codechecker.domain.model.CodeFile
import com.example.codechecker.domain.repository.CodeFileRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class FileViewModel(
    private val codeFileRepository: CodeFileRepository,
    private val currentUserId: Long
) : ViewModel() {
    
    // UI状态
    private val _uiState = MutableStateFlow(FileUiState())
    val uiState: StateFlow<FileUiState> = _uiState.asStateFlow()

    // 刷新触发器
    private val _refreshTrigger = MutableStateFlow(0)
    
    // 文件列表
    val files: Flow<List<CodeFile>> = codeFileRepository.getFilesByUser(currentUserId)
        .flatMapLatest { 
            codeFileRepository.getFilesByUser(currentUserId)
                .onStart { 
                    _uiState.update { it.copy(isLoading = true, error = null) } 
                }
                .catch { error ->
                    _uiState.update { it.copy(
                        isLoading = false,
                        error = error.message
                    ) }
                }
        }
        .onEach { files ->
            _uiState.update { it.copy(
                isLoading = false,
                fileCount = files.size
            ) }
        }
        .catch { error ->
            _uiState.update { it.copy(
                isLoading = false,
                error = error.message
            ) }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
    
    init {
        loadFiles()
    }
    
    // 加载文件
    fun loadFiles() {
        _refreshTrigger.update { it + 1 }
    }
    
    // 上传文件
    fun uploadFile(fileName: String, codeContent: String, displayName: String? = null) {
        viewModelScope.launch {
            _uiState.update { it.copy(isUploading = true, error = null) }
            
            val result = codeFileRepository.saveCodeFile(
                userId = currentUserId,
                fileName = fileName,
                codeContent = codeContent,
                displayName = displayName
            )
            
            result.fold(
                onSuccess = { fileId ->
                    _uiState.update { it.copy(
                        isUploading = false,
                        lastUploadedFileId = fileId,
                        showUploadSuccess = true
                    ) }
                    
                    // 3秒后隐藏成功消息
                    viewModelScope.launch {
                        kotlinx.coroutines.delay(3000)
                        _uiState.update { it.copy(showUploadSuccess = false) }
                    }
                },
                onFailure = { error ->
                    _uiState.update { it.copy(
                        isUploading = false,
                        error = error.message ?: "上传失败"
                    ) }
                }
            )
        }
    }
    
    // 删除文件
    fun deleteFile(fileId: Long) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            
            val result = codeFileRepository.deleteFile(fileId)
            result.fold(
                onSuccess = {
                    _uiState.update { it.copy(
                        isLoading = false,
                        showDeleteSuccess = true
                    ) }
                    
                    // 2秒后隐藏成功消息
                    kotlinx.coroutines.delay(2000)
                    _uiState.update { it.copy(showDeleteSuccess = false) }
                },
                onFailure = { error ->
                    _uiState.update { it.copy(
                        isLoading = false,
                        error = error.message ?: "删除失败"
                    ) }
                }
            )
        }
    }
    
    // 更新显示名称
    fun updateDisplayName(fileId: Long, displayName: String) {
        viewModelScope.launch {
            val result = codeFileRepository.updateDisplayName(fileId, displayName)
            result.fold(
                onSuccess = {
                    // 名称更新成功，不需要特殊处理
                },
                onFailure = { error ->
                    _uiState.update { it.copy(
                        error = error.message ?: "更新失败"
                    ) }
                }
            )
        }
    }
    
    // 搜索文件
    fun searchFiles(query: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            
            try {
                val results = codeFileRepository.searchFiles(currentUserId, query)
                _uiState.update { it.copy(
                    isLoading = false,
                    searchResults = results,
                    isSearching = query.isNotBlank()
                ) }
            } catch (e: Exception) {
                _uiState.update { it.copy(
                    isLoading = false,
                    error = e.message ?: "搜索失败"
                ) }
            }
        }
    }
    
    // 清除错误
    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }
    
    // 清除搜索
    fun clearSearch() {
        _uiState.update { it.copy(
            isSearching = false,
            searchResults = emptyList()
        ) }
    }
}

data class FileUiState(
    val isLoading: Boolean = false,
    val isUploading: Boolean = false,
    val isSearching: Boolean = false,
    val fileCount: Int = 0,
    val lastUploadedFileId: Long? = null,
    val showUploadSuccess: Boolean = false,
    val showDeleteSuccess: Boolean = false,
    val error: String? = null,
    val searchResults: List<CodeFile> = emptyList(),
    val plagiarismHistory: List<Any> = emptyList()
)