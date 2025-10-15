package com.trplayer.embyplayer.domain.repository

import androidx.paging.PagingData
import com.trplayer.embyplayer.data.model.MediaItem
import kotlinx.coroutines.flow.Flow

interface MediaRepository {
    
    /**
     * 获取媒体项的分页数据流
     * @param parentId 父级目录ID（可选）
     * @return 媒体项的分页数据流
     */
    suspend fun getMediaItems(parentId: String?): Flow<PagingData<MediaItem>>
    
    /**
     * 搜索媒体项的分页数据流
     * @param query 搜索关键词
     * @return 搜索结果的分页数据流
     */
    suspend fun searchMediaItems(query: String): Flow<PagingData<MediaItem>>
}