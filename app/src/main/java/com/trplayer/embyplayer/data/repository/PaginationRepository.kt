package com.trplayer.embyplayer.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.trplayer.embyplayer.data.model.MediaItem
import com.trplayer.embyplayer.data.remote.EmbyApiService
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
        const val DEFAULT_PAGE_SIZE = BuildConfig.PAGE_SIZE
        const val PRELOAD_THRESHOLD = BuildConfig.PRELOAD_THRESHOLD
    }
    
    /**
     * 获取媒体项的分页数据流
     */
    override suspend fun getMediaItems(parentId: String?): Flow<PagingData<MediaItem>> {
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
    override suspend fun searchMediaItems(query: String): Flow<PagingData<MediaItem>> {
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
}

/**
 * 媒体分页数据源
 */
class MediaPagingSource(
    private val apiService: EmbyApiService,
    private val userId: String,
    private val parentId: String?,
    private val serverBaseUrl: String
) : androidx.paging.PagingSource<Int, MediaItem>() {
    
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
            
            LoadResult.Page(
                data = mediaItems,
                prevKey = if (page > 0) page - 1 else null,
                nextKey = if (mediaItems.isNotEmpty()) page + 1 else null
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
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
) : androidx.paging.PagingSource<Int, MediaItem>() {
    
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, MediaItem> {
        return try {
            val page = params.key ?: 0
            val pageSize = params.loadSize
            
            val response = apiService.searchItems(
                userId = userId,
                query = query,
                startIndex = page * pageSize,
                limit = pageSize
            )
            
            val mediaItems = response.items.map { embyItem ->
                MediaItem.fromEmbyItem(embyItem, serverBaseUrl)
            }
            
            LoadResult.Page(
                data = mediaItems,
                prevKey = if (page > 0) page - 1 else null,
                nextKey = if (mediaItems.isNotEmpty()) page + 1 else null
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }
    
    override fun getRefreshKey(state: PagingState<Int, MediaItem>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }
}