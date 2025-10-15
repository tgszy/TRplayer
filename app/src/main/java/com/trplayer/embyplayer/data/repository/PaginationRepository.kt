package com.trplayer.embyplayer.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.trplayer.embyplayer.data.remote.EmblyApiService
import com.trplayer.embyplayer.domain.model.MediaItem
import com.trplayer.embyplayer.domain.repository.MediaRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * 分页加载仓库实现
 * 支持分页加载、预加载和缓存管理
 */
class PaginationRepository @Inject constructor(
    private val apiService: EmbyApiService
) : MediaRepository {
    
    companion object {
        const val DEFAULT_PAGE_SIZE = BuildConfig.PAGE_SIZE
        const val PRELOAD_THRESHOLD = BuildConfig.PRELOAD_THRESHOLD
    }
    
    /**
     * 获取媒体项的分页数据流
     */
    override fun getMediaItems(parentId: String?): Flow<PagingData<MediaItem>> {
        return Pager(
            config = PagingConfig(
                pageSize = DEFAULT_PAGE_SIZE,
                prefetchDistance = PRELOAD_THRESHOLD,
                enablePlaceholders = false
            ),
            pagingSourceFactory = {
                MediaPagingSource(apiService, parentId)
            }
        ).flow
    }
    
    /**
     * 搜索媒体项的分页数据流
     */
    override fun searchMediaItems(query: String): Flow<PagingData<MediaItem>> {
        return Pager(
            config = PagingConfig(
                pageSize = DEFAULT_PAGE_SIZE,
                prefetchDistance = PRELOAD_THRESHOLD,
                enablePlaceholders = false
            ),
            pagingSourceFactory = {
                SearchPagingSource(apiService, query)
            }
        ).flow
    }
}

/**
 * 媒体分页数据源
 */
class MediaPagingSource(
    private val apiService: EmbyApiService,
    private val parentId: String?
) : androidx.paging.PagingSource<Int, MediaItem>() {
    
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, MediaItem> {
        return try {
            val page = params.key ?: 0
            val pageSize = params.loadSize
            
            val response = apiService.getItems(
                parentId = parentId,
                startIndex = page * pageSize,
                limit = pageSize
            )
            
            val mediaItems = response.items.map { it.toMediaItem() }
            
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
    private val query: String
) : androidx.paging.PagingSource<Int, MediaItem>() {
    
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, MediaItem> {
        return try {
            val page = params.key ?: 0
            val pageSize = params.loadSize
            
            val response = apiService.searchItems(
                query = query,
                startIndex = page * pageSize,
                limit = pageSize
            )
            
            val mediaItems = response.items.map { it.toMediaItem() }
            
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