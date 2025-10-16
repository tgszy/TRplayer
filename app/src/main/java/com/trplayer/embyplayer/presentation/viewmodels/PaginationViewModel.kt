package com.trplayer.embyplayer.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.trplayer.embyplayer.data.model.MediaItem
import com.trplayer.embyplayer.data.repository.PaginationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * 分页加载ViewModel
 * 负责管理媒体列表的分页加载和预加载
 */
@HiltViewModel
class PaginationViewModel @Inject constructor(
    private val paginationRepository: PaginationRepository
) : ViewModel() {
    
    private val _paginationState = MutableStateFlow(PaginationState())
    val paginationState: StateFlow<PaginationState> = _paginationState.asStateFlow()
    
    private var currentMediaFlow: Flow<PagingData<MediaItem>>? = null
    
    /**
     * 获取媒体列表的分页数据流
     */
    fun getMediaPagingData(libraryId: String? = null): Flow<PagingData<MediaItem>> {
        return flow {
            val newFlow = paginationRepository.getMediaItems(libraryId)
                .cachedIn(viewModelScope)
            currentMediaFlow = newFlow
            emitAll(newFlow)
        }
    }
    
    /**
     * 搜索媒体内容的分页数据流
     */
    fun searchMediaPagingData(query: String): Flow<PagingData<MediaItem>> {
        return flow {
            val searchFlow = paginationRepository.searchMediaItems(query)
                .cachedIn(viewModelScope)
            emitAll(searchFlow)
        }
    }
    
    /**
     * 预加载指定位置的媒体项
     */
    fun preloadMediaItems(visibleItemIndices: List<Int>, totalItemCount: Int) {
        viewModelScope.launch {
            try {
                paginationRepository.preloadMediaItems(visibleItemIndices, totalItemCount)
                _paginationState.value = _paginationState.value.copy(
                    preloadStatus = "预加载完成: ${visibleItemIndices.size} 个项目"
                )
            } catch (e: Exception) {
                _paginationState.value = _paginationState.value.copy(
                    error = "预加载失败: ${e.message}"
                )
            }
        }
    }
    
    /**
     * 设置预加载阈值
     */
    fun setPreloadThreshold(threshold: Int) {
        viewModelScope.launch {
            try {
                paginationRepository.setPreloadThreshold(threshold)
                _paginationState.value = _paginationState.value.copy(
                    preloadThreshold = threshold
                )
            } catch (e: Exception) {
                _paginationState.value = _paginationState.value.copy(
                    error = "设置预加载阈值失败: ${e.message}"
                )
            }
        }
    }
    
    /**
     * 设置页面大小
     */
    fun setPageSize(size: Int) {
        viewModelScope.launch {
            try {
                paginationRepository.setPageSize(size)
                _paginationState.value = _paginationState.value.copy(
                    pageSize = size
                )
            } catch (e: Exception) {
                _paginationState.value = _paginationState.value.copy(
                    error = "设置页面大小失败: ${e.message}"
                )
            }
        }
    }
    
    /**
     * 清除错误信息
     */
    fun clearError() {
        _paginationState.value = _paginationState.value.copy(error = null)
    }
    
    /**
     * 清除预加载状态
     */
    fun clearPreloadStatus() {
        _paginationState.value = _paginationState.value.copy(preloadStatus = null)
    }
}

/**
 * 分页状态数据类
 */
data class PaginationState(
    val pageSize: Int = 20, // 默认页面大小，从BuildConfig.PAGE_SIZE获取
    val preloadThreshold: Int = 5, // 默认预加载阈值，从BuildConfig.PRELOAD_THRESHOLD获取
    val isLoading: Boolean = false,
    val preloadStatus: String? = null,
    val error: String? = null
)