package com.trplayer.embyplayer.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.trplayer.embyplayer.data.model.MediaItem
import com.trplayer.embyplayer.data.remote.api.EmbyApiService
import com.trplayer.embyplayer.domain.model.EmbyServer
import com.trplayer.embyplayer.domain.repository.EmbyRepository
import com.trplayer.embyplayer.domain.repository.MediaRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import javax.inject.Inject

/**
 * 分页加载仓库实现
 * 支持分页加载、预加载和缓存管理
 */
class PaginationRepository @Inject constructor(
    private val apiService: EmbyApiService,
    private val embyRepository: EmbyRepository,
    private val currentServer: EmbyServer?
) : MediaRepository {
    
    companion object {
        const val DEFAULT_PAGE_SIZE = 20
        const val PRELOAD_THRESHOLD = 5
    }
    
    /**
     * 获取媒体项的分页数据流
     */
    fun getMediaPagingData(parentId: String?): Flow<PagingData<MediaItem>> {
        val userId = embyRepository.getCurrentUser().first() ?: throw IllegalStateException("用户未登录")
        return Pager(
            config = PagingConfig(
                pageSize = DEFAULT_PAGE_SIZE,
                prefetchDistance = PRELOAD_THRESHOLD,
                enablePlaceholders = false
            ),
            pagingSourceFactory = {
                MediaPagingSource(apiService, userId, parentId, currentServer?.getBaseUrl() ?: "")
            }
        ).flow
    }
    
    /**
     * 搜索媒体项的分页数据流
     */
    fun searchMediaPagingData(query: String): Flow<PagingData<MediaItem>> {
        val userId = embyRepository.getCurrentUser().first() ?: throw IllegalStateException("用户未登录")
        return Pager(
            config = PagingConfig(
                pageSize = DEFAULT_PAGE_SIZE,
                prefetchDistance = PRELOAD_THRESHOLD,
                enablePlaceholders = false
            ),
            pagingSourceFactory = {
                SearchPagingSource(apiService, userId, query, currentServer?.getBaseUrl() ?: "")
            }
        ).flow
    }
    
    /**
     * 预加载指定位置的媒体项
     */
    suspend fun preloadMediaItems(visibleItemIndices: List<Int>, totalItemCount: Int) {
        // 实现预加载逻辑
    }
    
    /**
     * 设置预加载阈值
     */
    suspend fun setPreloadThreshold(threshold: Int) {
        // 实现设置预加载阈值逻辑
    }
    
    /**
     * 设置页面大小
     */
    suspend fun setPageSize(size: Int) {
        // 实现设置页面大小逻辑
    }
}

/**
 * 媒体分页数据源
 */
class MediaPagingSource(
    private val apiService: EmbyApiService,
    private val userId: String,
    private val parentId: String?,
    private val serverBaseUrl: String
) : PagingSource<Int, MediaItem>() {
    
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, MediaItem> {
        return try {
            val page = params.key ?: 0
            val pageSize = params.loadSize
            
            val response = apiService.getItems(
                userId = userId,
                parentId = parentId,
                startIndex = page * pageSize,
                limit = pageSize
            )
            
            val mediaItems = response.items.map { embyItem ->
                MediaItem.fromEmbyItem(embyItem, serverBaseUrl)
            }
            
            LoadResult.Page<Int, MediaItem>(
                data = mediaItems,
                prevKey = if (page > 0) page - 1 else null,
                nextKey = if (mediaItems.isNotEmpty()) page + 1 else null
            )
        } catch (e: Exception) {
            LoadResult.Error<Int, MediaItem>(e)
        }
    }
    
    override fun getRefreshKey(state: PagingState<Int, MediaItem>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }
}

/**
 * 搜索分页数据源
 */
class SearchPagingSource(
    private val apiService: EmbyApiService,
    private val userId: String,
    private val query: String,
    private val serverBaseUrl: String
) : PagingSource<Int, MediaItem>() {
    
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, MediaItem> {
        return try {
            val page = params.key ?: 0
            val pageSize = params.loadSize
            
            val response = apiService.searchItems(
                userId = userId,
                searchTerm = query,
                limit = pageSize
            )
            
            val mediaItems = response.items.map { embyItem ->
                MediaItem.fromEmbyItem(embyItem, serverBaseUrl)
            }
            
            LoadResult.Page<Int, MediaItem>(
                data = mediaItems,
                prevKey = if (page > 0) page - 1 else null,
                nextKey = if (mediaItems.isNotEmpty()) page + 1 else null
            )
        } catch (e: Exception) {
            LoadResult.Error<Int, MediaItem>(e)
        }
    }
    
    override fun getRefreshKey(state: PagingState<Int, MediaItem>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }
}