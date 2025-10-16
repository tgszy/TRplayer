package com.trplayer.embyplayer.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.trplayer.embyplayer.data.model.MediaItem
import com.trplayer.embyplayer.data.remote.api.EmbyApiService
import com.trplayer.embyplayer.domain.model.EmbyItem as DomainEmbyItem
import com.trplayer.embyplayer.domain.model.MediaSource as DomainMediaSource
import com.trplayer.embyplayer.domain.model.UserData as DomainUserData
import com.trplayer.embyplayer.domain.model.EmbyServer
import com.trplayer.embyplayer.domain.repository.EmbyRepository
import com.trplayer.embyplayer.domain.repository.MediaRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.emitAll
import javax.inject.Inject

/**
 * 分页加载仓库实现
 * 支持分页加载、预加载和缓存管理
 */
class PaginationRepository @Inject constructor(
    private val apiService: EmbyApiService,
    private val embyRepository: EmbyRepository
) : MediaRepository {
    
    companion object {
        const val DEFAULT_PAGE_SIZE = 20
        const val PRELOAD_THRESHOLD = 5
    }
    
    /**
     * 获取媒体项的分页数据流
     */
    override suspend fun getMediaItems(parentId: String?): Flow<PagingData<MediaItem>> {
        val userId = embyRepository.getCurrentUser().first() ?: throw IllegalStateException("用户未登录")
        val currentServer = embyRepository.getCurrentServer().first()
        val serverBaseUrl = currentServer?.getBaseUrl() ?: ""
        return Pager(
            config = PagingConfig(
                pageSize = DEFAULT_PAGE_SIZE,
                prefetchDistance = PRELOAD_THRESHOLD,
                enablePlaceholders = false
            ),
            pagingSourceFactory = {
                MediaPagingSource(apiService, userId, parentId, serverBaseUrl)
            }
        ).flow
    }
    
    /**
     * 搜索媒体项的分页数据流
     */
    override suspend fun searchMediaItems(query: String): Flow<PagingData<MediaItem>> {
        val userId = embyRepository.getCurrentUser().first() ?: throw IllegalStateException("用户未登录")
        val currentServer = embyRepository.getCurrentServer().first()
        val serverBaseUrl = currentServer?.getBaseUrl() ?: ""
        return Pager(
            config = PagingConfig(
                pageSize = DEFAULT_PAGE_SIZE,
                prefetchDistance = PRELOAD_THRESHOLD,
                enablePlaceholders = false
            ),
            pagingSourceFactory = {
                SearchPagingSource(apiService, userId, query, serverBaseUrl)
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

    private fun convertRemoteEmbyItemToDomain(remoteItem: com.trplayer.embyplayer.data.remote.model.EmbyItem): DomainEmbyItem {
        return DomainEmbyItem(
            id = remoteItem.id,
            name = remoteItem.name,
            type = remoteItem.type,
            mediaType = remoteItem.mediaType,
            overview = remoteItem.overview,
            productionYear = remoteItem.productionYear,
            premiereDate = remoteItem.premiereDate,
            runtimeTicks = remoteItem.runTimeTicks,
            seriesName = remoteItem.seriesName,
            seasonName = remoteItem.seasonName,
            episodeNumber = remoteItem.indexNumber,
            seasonNumber = remoteItem.parentIndexNumber,
            communityRating = remoteItem.communityRating,
            officialRating = remoteItem.officialRating,
            imageTags = remoteItem.imageTags,
            backdropImageTags = remoteItem.backdropImageTags,
            mediaSources = remoteItem.mediaSources?.map { convertRemoteMediaSourceToDomain(it) },
            chapters = remoteItem.chapters?.map { convertRemoteChapterToDomain(it) },
            userData = remoteItem.userData?.let { convertRemoteUserDataToDomain(it) }
        )
    }
    
    private fun convertRemoteMediaSourceToDomain(remoteSource: com.trplayer.embyplayer.data.remote.model.MediaSource): DomainMediaSource {
        return DomainMediaSource(
            id = remoteSource.id,
            path = remoteSource.path,
            protocol = remoteSource.protocol,
            container = remoteSource.container,
            size = remoteSource.size,
            name = remoteSource.name,
            isRemote = remoteSource.isRemote,
            runtimeTicks = remoteSource.runTimeTicks,
            supportsTranscoding = remoteSource.supportsTranscoding,
            supportsDirectStream = remoteSource.supportsDirectStream,
            supportsDirectPlay = remoteSource.supportsDirectPlay,
            videoStream = remoteSource.videoStream?.let { convertRemoteVideoStreamToDomain(it) },
            audioStream = remoteSource.audioStream?.let { convertRemoteAudioStreamToDomain(it) },
            mediaStreams = remoteSource.mediaStreams?.map { convertRemoteMediaStreamToDomain(it) }
        )
    }
    
    private fun convertRemoteVideoStreamToDomain(remoteStream: com.trplayer.embyplayer.data.remote.model.VideoStream): com.trplayer.embyplayer.domain.model.VideoStream {
        return com.trplayer.embyplayer.domain.model.VideoStream(
            codec = remoteStream.codec,
            width = remoteStream.width,
            height = remoteStream.height,
            averageFrameRate = remoteStream.averageFrameRate,
            realFrameRate = remoteStream.realFrameRate,
            profile = remoteStream.profile,
            level = remoteStream.level,
            pixelFormat = remoteStream.pixelFormat,
            refFrames = remoteStream.refFrames
        )
    }
    
    private fun convertRemoteAudioStreamToDomain(remoteStream: com.trplayer.embyplayer.data.remote.model.AudioStream): com.trplayer.embyplayer.domain.model.AudioStream {
        return com.trplayer.embyplayer.domain.model.AudioStream(
            codec = remoteStream.codec,
            channels = remoteStream.channels,
            sampleRate = remoteStream.sampleRate,
            bitrate = remoteStream.bitrate
        )
    }
    
    private fun convertRemoteMediaStreamToDomain(remoteStream: com.trplayer.embyplayer.data.remote.model.MediaStream): com.trplayer.embyplayer.domain.model.MediaStream {
        return com.trplayer.embyplayer.domain.model.MediaStream(
            index = remoteStream.index,
            type = remoteStream.type,
            codec = remoteStream.codec,
            language = remoteStream.language,
            title = remoteStream.title,
            displayTitle = remoteStream.displayTitle,
            isDefault = remoteStream.isDefault,
            isForced = remoteStream.isForced,
            isHearingImpaired = remoteStream.isHearingImpaired,
            width = remoteStream.width,
            height = remoteStream.height,
            aspectRatio = remoteStream.aspectRatio,
            averageFrameRate = remoteStream.averageFrameRate,
            realFrameRate = remoteStream.realFrameRate,
            profile = remoteStream.profile,
            level = remoteStream.level,
            channels = remoteStream.channels,
            sampleRate = remoteStream.sampleRate,
            bitrate = remoteStream.bitrate,
            bitDepth = remoteStream.bitDepth
        )
    }
    
    private fun convertRemoteChapterToDomain(remoteChapter: com.trplayer.embyplayer.data.remote.model.Chapter): com.trplayer.embyplayer.domain.model.Chapter {
        return com.trplayer.embyplayer.domain.model.Chapter(
            startPositionTicks = remoteChapter.startPositionTicks,
            name = remoteChapter.name,
            imagePath = remoteChapter.imagePath
        )
    }
    
    private fun convertRemoteUserDataToDomain(remoteUserData: com.trplayer.embyplayer.data.remote.model.UserData): DomainUserData {
        return DomainUserData(
            rating = remoteUserData.rating,
            playedPercentage = remoteUserData.playedPercentage,
            unplayedItemCount = remoteUserData.unplayedItemCount,
            playbackPositionTicks = remoteUserData.playbackPositionTicks,
            playCount = remoteUserData.playCount,
            isFavorite = remoteUserData.isFavorite,
            likes = remoteUserData.likes,
            lastPlayedDate = remoteUserData.lastPlayedDate,
            played = remoteUserData.played,
            key = remoteUserData.key
        )
    }
    
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
            
            val mediaItems = response.body()?.items?.map { embyItem ->
                val domainItem = convertRemoteEmbyItemToDomain(embyItem)
                MediaItem.fromEmbyItem(domainItem, serverBaseUrl)
            } ?: emptyList()
            
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

    private fun convertRemoteEmbyItemToDomain(remoteItem: com.trplayer.embyplayer.data.remote.model.EmbyItem): DomainEmbyItem {
        return DomainEmbyItem(
            id = remoteItem.id,
            name = remoteItem.name,
            type = remoteItem.type,
            mediaType = remoteItem.mediaType,
            overview = remoteItem.overview,
            productionYear = remoteItem.productionYear,
            premiereDate = remoteItem.premiereDate,
            runtimeTicks = remoteItem.runTimeTicks,
            seriesName = remoteItem.seriesName,
            seasonName = remoteItem.seasonName,
            episodeNumber = remoteItem.indexNumber,
            seasonNumber = remoteItem.parentIndexNumber,
            communityRating = remoteItem.communityRating,
            officialRating = remoteItem.officialRating,
            imageTags = remoteItem.imageTags,
            backdropImageTags = remoteItem.backdropImageTags,
            mediaSources = remoteItem.mediaSources?.map { convertRemoteMediaSourceToDomain(it) },
            chapters = remoteItem.chapters?.map { convertRemoteChapterToDomain(it) },
            userData = remoteItem.userData?.let { convertRemoteUserDataToDomain(it) }
        )
    }
    
    private fun convertRemoteMediaSourceToDomain(remoteSource: com.trplayer.embyplayer.data.remote.model.MediaSource): DomainMediaSource {
        return DomainMediaSource(
            id = remoteSource.id,
            path = remoteSource.path,
            protocol = remoteSource.protocol,
            container = remoteSource.container,
            size = remoteSource.size,
            name = remoteSource.name,
            isRemote = remoteSource.isRemote,
            runtimeTicks = remoteSource.runTimeTicks,
            supportsTranscoding = remoteSource.supportsTranscoding,
            supportsDirectStream = remoteSource.supportsDirectStream,
            supportsDirectPlay = remoteSource.supportsDirectPlay,
            videoStream = remoteSource.videoStream?.let { convertRemoteVideoStreamToDomain(it) },
            audioStream = remoteSource.audioStream?.let { convertRemoteAudioStreamToDomain(it) },
            mediaStreams = remoteSource.mediaStreams?.map { convertRemoteMediaStreamToDomain(it) }
        )
    }
    
    private fun convertRemoteVideoStreamToDomain(remoteStream: com.trplayer.embyplayer.data.remote.model.VideoStream): com.trplayer.embyplayer.domain.model.VideoStream {
        return com.trplayer.embyplayer.domain.model.VideoStream(
            codec = remoteStream.codec,
            width = remoteStream.width,
            height = remoteStream.height,
            averageFrameRate = remoteStream.averageFrameRate,
            realFrameRate = remoteStream.realFrameRate,
            profile = remoteStream.profile,
            level = remoteStream.level,
            pixelFormat = remoteStream.pixelFormat,
            refFrames = remoteStream.refFrames
        )
    }
    
    private fun convertRemoteAudioStreamToDomain(remoteStream: com.trplayer.embyplayer.data.remote.model.AudioStream): com.trplayer.embyplayer.domain.model.AudioStream {
        return com.trplayer.embyplayer.domain.model.AudioStream(
            codec = remoteStream.codec,
            channels = remoteStream.channels,
            sampleRate = remoteStream.sampleRate,
            bitrate = remoteStream.bitrate
        )
    }
    
    private fun convertRemoteMediaStreamToDomain(remoteStream: com.trplayer.embyplayer.data.remote.model.MediaStream): com.trplayer.embyplayer.domain.model.MediaStream {
        return com.trplayer.embyplayer.domain.model.MediaStream(
            index = remoteStream.index,
            type = remoteStream.type,
            codec = remoteStream.codec,
            language = remoteStream.language,
            title = remoteStream.title,
            displayTitle = remoteStream.displayTitle,
            isDefault = remoteStream.isDefault,
            isForced = remoteStream.isForced,
            isHearingImpaired = remoteStream.isHearingImpaired,
            width = remoteStream.width,
            height = remoteStream.height,
            aspectRatio = remoteStream.aspectRatio,
            averageFrameRate = remoteStream.averageFrameRate,
            realFrameRate = remoteStream.realFrameRate,
            profile = remoteStream.profile,
            level = remoteStream.level,
            channels = remoteStream.channels,
            sampleRate = remoteStream.sampleRate,
            bitrate = remoteStream.bitrate,
            bitDepth = remoteStream.bitDepth
        )
    }
    
    private fun convertRemoteChapterToDomain(remoteChapter: com.trplayer.embyplayer.data.remote.model.Chapter): com.trplayer.embyplayer.domain.model.Chapter {
        return com.trplayer.embyplayer.domain.model.Chapter(
            startPositionTicks = remoteChapter.startPositionTicks,
            name = remoteChapter.name,
            imagePath = remoteChapter.imagePath
        )
    }
    
    private fun convertRemoteUserDataToDomain(remoteUserData: com.trplayer.embyplayer.data.remote.model.UserData): DomainUserData {
        return DomainUserData(
            rating = remoteUserData.rating,
            playedPercentage = remoteUserData.playedPercentage,
            unplayedItemCount = remoteUserData.unplayedItemCount,
            playbackPositionTicks = remoteUserData.playbackPositionTicks,
            playCount = remoteUserData.playCount,
            isFavorite = remoteUserData.isFavorite,
            likes = remoteUserData.likes,
            lastPlayedDate = remoteUserData.lastPlayedDate,
            played = remoteUserData.played,
            key = remoteUserData.key
        )
    }
    
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, MediaItem> {
        return try {
            val page = params.key ?: 0
            val pageSize = params.loadSize
            
            val response = apiService.searchItems(
                userId = userId,
                searchTerm = query,
                limit = pageSize
            )
            
            val mediaItems = response.body()?.items?.map { embyItem ->
                val domainItem = convertRemoteEmbyItemToDomain(embyItem)
                MediaItem.fromEmbyItem(domainItem, serverBaseUrl)
            } ?: emptyList()
            
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