package com.trplayer.embyplayer.data.repository

import com.trplayer.embyplayer.data.local.datastore.EmbyDataStore
import com.trplayer.embyplayer.data.remote.api.EmbyApiService
import com.trplayer.embyplayer.data.remote.model.EmbyItem as RemoteEmbyItem
import com.trplayer.embyplayer.data.remote.model.AuthenticationResult
import com.trplayer.embyplayer.data.remote.model.EmbyUser as RemoteEmbyUser
import com.trplayer.embyplayer.data.remote.model.PlaybackInfoResponse
import com.trplayer.embyplayer.data.remote.model.PlaybackStartRequest
import com.trplayer.embyplayer.data.remote.model.PlaybackProgressRequest
import com.trplayer.embyplayer.data.remote.model.PlaybackStopRequest
import com.trplayer.embyplayer.domain.model.EmbyItem
import com.trplayer.embyplayer.domain.model.EmbyUser
import com.trplayer.embyplayer.domain.model.EmbyLibrary
import com.trplayer.embyplayer.domain.model.EmbyServer
import com.trplayer.embyplayer.domain.model.MediaSource
import com.trplayer.embyplayer.domain.model.VideoStream
import com.trplayer.embyplayer.domain.model.AudioStream
import com.trplayer.embyplayer.domain.model.MediaStream
import com.trplayer.embyplayer.domain.model.Chapter
import com.trplayer.embyplayer.domain.model.UserData
import com.trplayer.embyplayer.domain.repository.EmbyRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import retrofit2.HttpException
import javax.inject.Inject

/**
 * Emby存储库实现类
 * 负责协调远程API调用和本地数据存储
 */
class EmbyRepositoryImpl @Inject constructor(
    private val apiService: EmbyApiService,
    private val dataStore: EmbyDataStore
) : EmbyRepository {

    // ==================== 用户认证相关 ====================

    override suspend fun authenticateUser(userId: String, password: String?): Result<AuthenticationResult> {
        return try {
            val response = apiService.authenticateUser(userId, password)
            if (response.isSuccessful) {
                response.body()?.let { authResult ->
                    // 保存认证信息到本地存储
                    dataStore.setAccessToken(authResult.accessToken)
                    dataStore.setUserId(authResult.user.id)
                    dataStore.setUserName(authResult.user.name)
                    Result.success(authResult)
                } ?: Result.failure(Exception("认证响应为空"))
            } else {
                Result.failure(HttpException(response))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getPublicUsers(): Result<List<EmbyUser>> {
        return try {
            val response = apiService.getPublicUsers()
            if (response.isSuccessful) {
                val remoteUsers = response.body() ?: emptyList()
                val domainUsers = remoteUsers.map { remoteUser ->
                    EmbyUser(
                        id = remoteUser.id,
                        name = remoteUser.name,
                        serverId = remoteUser.serverId,
                        hasPassword = remoteUser.hasPassword,
                        lastLoginDate = remoteUser.lastLoginDate
                    )
                }
                Result.success(domainUsers)
            } else {
                Result.failure(HttpException(response))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getUserById(userId: String): Result<EmbyUser> {
        return try {
            val response = apiService.getUserById(userId)
            if (response.isSuccessful) {
                response.body()?.let { remoteUser ->
                    val domainUser = EmbyUser(
                        id = remoteUser.id,
                        name = remoteUser.name,
                        serverId = remoteUser.serverId,
                        hasPassword = remoteUser.hasPassword,
                        lastLoginDate = remoteUser.lastLoginDate
                    )
                    Result.success(domainUser)
                } ?: Result.failure(Exception("用户不存在"))
            } else {
                Result.failure(HttpException(response))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // ==================== 媒体库和媒体项相关 ====================

    override suspend fun getUserLibraries(userId: String): Result<List<EmbyLibrary>> {
        return try {
            val response = apiService.getUserViews(userId)
            if (response.isSuccessful) {
                val libraries = response.body()?.items?.map { remoteLibrary ->
                    EmbyLibrary(
                        id = remoteLibrary.id,
                        name = remoteLibrary.name,
                        type = remoteLibrary.type,
                        collectionType = remoteLibrary.collectionType
                    )
                } ?: emptyList()
                Result.success(libraries)
            } else {
                Result.failure(HttpException(response))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getLibraryItems(
        userId: String,
        parentId: String?,
        itemTypes: List<String>?,
        startIndex: Int,
        limit: Int
    ): Result<List<EmbyItem>> {
        return try {
            val response = apiService.getItems(
                userId = userId,
                parentId = parentId,
                includeItemTypes = itemTypes?.joinToString(","),
                startIndex = startIndex,
                limit = limit
            )
            if (response.isSuccessful) {
                val items = response.body()?.items?.map { remoteItem ->
                    convertToDomainItem(remoteItem)
                } ?: emptyList()
                Result.success(items)
            } else {
                Result.failure(HttpException(response))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getItemDetails(userId: String, itemId: String): Result<EmbyItem> {
        return try {
            val response = apiService.getItemDetails(userId, itemId)
            if (response.isSuccessful) {
                response.body()?.let { remoteItem ->
                    Result.success(convertToDomainItem(remoteItem))
                } ?: Result.failure(Exception("媒体项不存在"))
            } else {
                Result.failure(HttpException(response))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getLatestItems(userId: String, limit: Int): Result<List<EmbyItem>> {
        return try {
            val response = apiService.getLatestItems(userId, limit)
            if (response.isSuccessful) {
                val items = response.body()?.map { remoteItem ->
                    convertToDomainItem(remoteItem)
                } ?: emptyList()
                Result.success(items)
            } else {
                Result.failure(HttpException(response))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getResumeItems(userId: String, limit: Int): Result<List<EmbyItem>> {
        return try {
            val response = apiService.getResumeItems(userId, limit)
            if (response.isSuccessful) {
                val items = response.body()?.items?.map { remoteItem ->
                    convertToDomainItem(remoteItem)
                } ?: emptyList()
                Result.success(items)
            } else {
                Result.failure(HttpException(response))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // ==================== 搜索相关 ====================

    override suspend fun searchItems(userId: String, searchTerm: String, limit: Int): Result<List<EmbyItem>> {
        return try {
            val response = apiService.searchItems(userId, searchTerm, limit)
            if (response.isSuccessful) {
                val items = response.body()?.items?.map { remoteItem ->
                    convertToDomainItem(remoteItem)
                } ?: emptyList()
                Result.success(items)
            } else {
                Result.failure(HttpException(response))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // ==================== 播放控制相关 ====================

    override suspend fun getPlaybackInfo(userId: String, itemId: String): Result<PlaybackInfoResponse> {
        return try {
            val response = apiService.getPlaybackInfo(itemId, userId)
            if (response.isSuccessful) {
                response.body()?.let { Result.success(it) } ?: Result.failure(Exception("播放信息获取失败"))
            } else {
                Result.failure(HttpException(response))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getPlaybackUrl(
        userId: String,
        itemId: String,
        mediaSourceId: String,
        startTimeTicks: Long?
    ): Result<String> {
        return try {
            // 获取当前服务器信息
            val server = dataStore.getCurrentServer()
            val accessToken = dataStore.getAccessToken()
            
            if (server == null || accessToken == null) {
                return Result.failure(Exception("未连接到服务器或未认证"))
            }
            
            // 构建播放URL
            val baseUrl = server.getEmbyUrl()
            val urlBuilder = StringBuilder("$baseUrl/Videos/$itemId/stream")
            
            // 添加查询参数
            urlBuilder.append("?api_key=$accessToken")
            urlBuilder.append("&MediaSourceId=$mediaSourceId")
            urlBuilder.append("&Static=true")
            
            if (startTimeTicks != null) {
                urlBuilder.append("&StartTimeTicks=$startTimeTicks")
            }
            
            // 添加设备信息参数
            urlBuilder.append("&DeviceId=TRPlayer-Android")
            urlBuilder.append("&Device=Android")
            
            Result.success(urlBuilder.toString())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun reportPlaybackStart(request: PlaybackStartRequest): Result<Unit> {
        return try {
            val response = apiService.reportPlaybackStart(request)
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(HttpException(response))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun reportPlaybackProgress(request: PlaybackProgressRequest): Result<Unit> {
        return try {
            val response = apiService.reportPlaybackProgress(request)
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(HttpException(response))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun reportPlaybackStopped(request: PlaybackStopRequest): Result<Unit> {
        return try {
            val response = apiService.reportPlaybackStopped(request)
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(HttpException(response))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // ==================== 本地数据存储相关 ====================

    override fun getCurrentUser(): Flow<String?> = dataStore.userId

    override fun getCurrentServer(): Flow<EmbyServer?> = dataStore.currentServer

    override suspend fun setCurrentUser(userId: String, userName: String) {
        dataStore.setUserId(userId)
        dataStore.setUserName(userName)
    }

    override suspend fun setCurrentServer(server: EmbyServer) {
        dataStore.setCurrentServer(server)
    }

    override suspend fun clearAuthentication() {
        dataStore.clearAll()
    }

    // ==================== 服务器管理相关 ====================

    override suspend fun getServers(): List<EmbyServer> {
        return dataStore.getServers()
    }

    override suspend fun setServers(servers: List<EmbyServer>) {
        dataStore.setServers(servers)
    }

    // ==================== 私有辅助方法 ====================

    /**
     * 将远程API返回的媒体项转换为域模型
     */
    private fun convertToDomainItem(remoteItem: RemoteEmbyItem): EmbyItem {
        return EmbyItem(
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
            mediaSources = remoteItem.mediaSources?.map { remoteSource ->
                MediaSource(
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
                    videoStream = remoteSource.videoStream?.let { remoteVideo ->
                        VideoStream(
                            codec = remoteVideo.codec,
                            width = remoteVideo.width,
                            height = remoteVideo.height,
                            averageFrameRate = remoteVideo.averageFrameRate,
                            realFrameRate = remoteVideo.realFrameRate,
                            profile = remoteVideo.profile,
                            level = remoteVideo.level,
                            pixelFormat = remoteVideo.pixelFormat,
                            refFrames = remoteVideo.refFrames
                        )
                    },
                    audioStream = remoteSource.audioStream?.let { remoteAudio ->
                        AudioStream(
                            codec = remoteAudio.codec,
                            channels = remoteAudio.channels,
                            sampleRate = remoteAudio.sampleRate,
                            bitrate = remoteAudio.bitrate
                        )
                    },
                    mediaStreams = remoteSource.mediaStreams?.map { remoteStream ->
                        MediaStream(
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
                )
            },
            chapters = remoteItem.chapters?.map { remoteChapter ->
                Chapter(
                    startPositionTicks = remoteChapter.startPositionTicks,
                    name = remoteChapter.name,
                    imagePath = remoteChapter.imagePath
                )
            },
            userData = remoteItem.userData?.let { remoteUserData ->
                UserData(
                    rating = remoteUserData.rating,
                    playedPercentage = remoteUserData.playedPercentage,
                    playbackPositionTicks = remoteUserData.playbackPositionTicks,
                    playCount = remoteUserData.playCount,
                    isFavorite = remoteUserData.isFavorite,
                    lastPlayedDate = remoteUserData.lastPlayedDate,
                    played = remoteUserData.played
                )
            }
        )
    }
}