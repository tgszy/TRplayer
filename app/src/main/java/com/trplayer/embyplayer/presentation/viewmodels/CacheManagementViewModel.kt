package com.trplayer.embyplayer.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.trplayer.embyplayer.data.local.cache.CacheManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * 缓存管理ViewModel
 * 负责缓存信息的加载和清理操作
 */
@HiltViewModel
class CacheManagementViewModel @Inject constructor(
    private val cacheManager: CacheManager
) : ViewModel() {
    
    private val _cacheState = MutableStateFlow(CacheState())
    val cacheState: StateFlow<CacheState> = _cacheState.asStateFlow()
    
    /**
     * 加载缓存信息
     */
    fun loadCacheInfo() {
        viewModelScope.launch {
            _cacheState.value = _cacheState.value.copy(isLoading = true)
            
            try {
                val cacheSize = cacheManager.getTotalCacheSize()
                val mediaCacheSize = cacheManager.getMediaCacheSize()
                val imageCacheSize = cacheManager.getImageCacheSize()
                val tempFilesSize = cacheManager.getTempFilesSize()
                val autoCleanEnabled = cacheManager.isAutoCleanEnabled()
                
                _cacheState.value = CacheState(
                    cacheSize = cacheSize,
                    mediaCacheSize = mediaCacheSize,
                    imageCacheSize = imageCacheSize,
                    tempFilesSize = tempFilesSize,
                    autoCleanEnabled = autoCleanEnabled,
                    isLoading = false
                )
            } catch (e: Exception) {
                _cacheState.value = _cacheState.value.copy(
                    error = "加载缓存信息失败: ${e.message}",
                    isLoading = false
                )
            }
        }
    }
    
    /**
     * 清除所有缓存
     */
    fun clearAllCache() {
        viewModelScope.launch {
            try {
                cacheManager.clearAllCache()
                loadCacheInfo() // 重新加载缓存信息
            } catch (e: Exception) {
                _cacheState.value = _cacheState.value.copy(
                    error = "清除缓存失败: ${e.message}"
                )
            }
        }
    }
    
    /**
     * 清除媒体缓存
     */
    fun clearMediaCache() {
        viewModelScope.launch {
            try {
                cacheManager.clearMediaCache()
                loadCacheInfo() // 重新加载缓存信息
            } catch (e: Exception) {
                _cacheState.value = _cacheState.value.copy(
                    error = "清除媒体缓存失败: ${e.message}"
                )
            }
        }
    }
    
    /**
     * 清除图片缓存
     */
    fun clearImageCache() {
        viewModelScope.launch {
            try {
                cacheManager.clearImageCache()
                loadCacheInfo() // 重新加载缓存信息
            } catch (e: Exception) {
                _cacheState.value = _cacheState.value.copy(
                    error = "清除图片缓存失败: ${e.message}"
                )
            }
        }
    }
    
    /**
     * 清除临时文件
     */
    fun clearTempFiles() {
        viewModelScope.launch {
            try {
                cacheManager.clearTempFiles()
                loadCacheInfo() // 重新加载缓存信息
            } catch (e: Exception) {
                _cacheState.value = _cacheState.value.copy(
                    error = "清除临时文件失败: ${e.message}"
                )
            }
        }
    }
    
    /**
     * 设置自动清理开关
     */
    fun setAutoCleanEnabled(enabled: Boolean) {
        viewModelScope.launch {
            try {
                cacheManager.setAutoCleanEnabled(enabled)
                _cacheState.value = _cacheState.value.copy(
                    autoCleanEnabled = enabled
                )
            } catch (e: Exception) {
                _cacheState.value = _cacheState.value.copy(
                    error = "设置自动清理失败: ${e.message}"
                )
            }
        }
    }
    
    /**
     * 清除错误信息
     */
    fun clearError() {
        _cacheState.value = _cacheState.value.copy(error = null)
    }
}

/**
 * 缓存状态数据类
 */
data class CacheState(
    val cacheSize: Long = 0L,
    val mediaCacheSize: Long = 0L,
    val imageCacheSize: Long = 0L,
    val tempFilesSize: Long = 0L,
    val autoCleanEnabled: Boolean = false,
    val isLoading: Boolean = false,
    val error: String? = null
)