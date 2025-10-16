package com.trplayer.embyplayer.data.remote.api

import com.google.gson.annotations.SerializedName
import com.trplayer.embyplayer.data.remote.model.*
import retrofit2.Response
import retrofit2.http.*

interface EmbyApiService {

    // Authentication
    @POST("Users/{userId}/Authenticate")
    suspend fun authenticateUser(
        @Path("userId") userId: String,
        @Query("pw") password: String? = null,
        @Query("password") passwordMd5: String? = null
    ): Response<AuthenticationResult>

    @GET("Users/Public")
    suspend fun getPublicUsers(): Response<List<EmbyUser>>

    @GET("Users/{userId}")
    suspend fun getUserById(
        @Path("userId") userId: String
    ): Response<EmbyUser>

    // Libraries
    @GET("Users/{userId}/Views")
    suspend fun getUserViews(
        @Path("userId") userId: String,
        @Query("IncludeExternalContent") includeExternalContent: Boolean = false
    ): Response<EmbyLibraryResponse>

    @GET("Users/{userId}/Items")
    suspend fun getItems(
        @Path("userId") userId: String,
        @Query("ParentId") parentId: String? = null,
        @Query("SortBy") sortBy: String = "SortName",
        @Query("SortOrder") sortOrder: String = "Ascending",
        @Query("IncludeItemTypes") includeItemTypes: String? = null,
        @Query("Recursive") recursive: Boolean = true,
        @Query("StartIndex") startIndex: Int = 0,
        @Query("Limit") limit: Int = 100,
        @Query("Fields") fields: String = "BasicSyncInfo,CanDelete,Container,PrimaryImageAspectRatio,ProductionYear,Status,EndDate"
    ): Response<EmbyItemsResponse>

    @GET("Users/{userId}/Items/{itemId}")
    suspend fun getItemDetails(
        @Path("userId") userId: String,
        @Path("itemId") itemId: String,
        @Query("Fields") fields: String = "MediaSources,Chapters,Trickplay"
    ): Response<EmbyItem>

    @GET("Users/{userId}/Items/Latest")
    suspend fun getLatestItems(
        @Path("userId") userId: String,
        @Query("Limit") limit: Int = 20,
        @Query("Fields") fields: String = "BasicSyncInfo,CanDelete,Container,PrimaryImageAspectRatio,ProductionYear,Status,EndDate"
    ): Response<List<EmbyItem>>

    @GET("Users/{userId}/Items/Resume")
    suspend fun getResumeItems(
        @Path("userId") userId: String,
        @Query("Limit") limit: Int = 20,
        @Query("MediaTypes") mediaTypes: String = "Video,Audio",
        @Query("Fields") fields: String = "BasicSyncInfo,CanDelete,Container,PrimaryImageAspectRatio,ProductionYear,Status,EndDate"
    ): Response<EmbyItemsResponse>

    // Playback
    @POST("Sessions/Playing")
    suspend fun reportPlaybackStart(
        @Body request: PlaybackStartRequest
    ): Response<Unit>

    @POST("Sessions/Playing/Progress")
    suspend fun reportPlaybackProgress(
        @Body request: PlaybackProgressRequest
    ): Response<Unit>

    @POST("Sessions/Playing/Stopped")
    suspend fun reportPlaybackStopped(
        @Body request: PlaybackStopRequest
    ): Response<Unit>

    @GET("Items/{itemId}/PlaybackInfo")
    suspend fun getPlaybackInfo(
        @Path("itemId") itemId: String,
        @Query("UserId") userId: String,
        @Query("StartTimeTicks") startTimeTicks: Long? = null,
        @Query("MaxStreamingBitrate") maxStreamingBitrate: Int? = null,
        @Query("DeviceProfile") deviceProfile: String? = null
    ): Response<PlaybackInfoResponse>

    // Images
    @GET("Items/{itemId}/Images/{imageType}")
    suspend fun getItemImage(
        @Path("itemId") itemId: String,
        @Path("imageType") imageType: String,
        @Query("MaxWidth") maxWidth: Int? = null,
        @Query("MaxHeight") maxHeight: Int? = null,
        @Query("Quality") quality: Int = 90,
        @Query("Tag") tag: String? = null
    ): Response<Unit>

    // Search
    @GET("Users/{userId}/Items")
    suspend fun searchItems(
        @Path("userId") userId: String,
        @Query("SearchTerm") searchTerm: String,
        @Query("IncludeItemTypes") includeItemTypes: String? = null,
        @Query("Recursive") recursive: Boolean = true,
        @Query("Limit") limit: Int = 20,
        @Query("Fields") fields: String = "BasicSyncInfo,CanDelete,Container,PrimaryImageAspectRatio,ProductionYear,Status,EndDate"
    ): Response<EmbyItemsResponse>
}

// Response wrapper classes
data class EmbyLibraryResponse(
    @SerializedName("Items") val items: List<EmbyLibrary>,
    @SerializedName("TotalRecordCount") val totalRecordCount: Int
)

data class EmbyItemsResponse(
    @SerializedName("Items") val items: List<EmbyItem>,
    @SerializedName("TotalRecordCount") val totalRecordCount: Int,
    @SerializedName("StartIndex") val startIndex: Int
)

// Playback request bodies
data class PlaybackStartRequest(
    @SerializedName("ItemId") val itemId: String,
    @SerializedName("SessionId") val sessionId: String,
    @SerializedName("MediaSourceId") val mediaSourceId: String,
    @SerializedName("CanSeek") val canSeek: Boolean,
    @SerializedName("IsPaused") val isPaused: Boolean,
    @SerializedName("IsMuted") val isMuted: Boolean,
    @SerializedName("PositionTicks") val positionTicks: Long,
    @SerializedName("PlaybackStartTimeTicks") val playbackStartTimeTicks: Long,
    @SerializedName("VolumeLevel") val volumeLevel: Int? = null,
    @SerializedName("AudioStreamIndex") val audioStreamIndex: Int? = null,
    @SerializedName("SubtitleStreamIndex") val subtitleStreamIndex: Int? = null,
    @SerializedName("PlayMethod") val playMethod: String,
    @SerializedName("LiveStreamId") val liveStreamId: String? = null,
    @SerializedName("PlaySessionId") val playSessionId: String
)

data class PlaybackProgressRequest(
    @SerializedName("ItemId") val itemId: String,
    @SerializedName("SessionId") val sessionId: String,
    @SerializedName("MediaSourceId") val mediaSourceId: String,
    @SerializedName("CanSeek") val canSeek: Boolean,
    @SerializedName("IsPaused") val isPaused: Boolean,
    @SerializedName("IsMuted") val isMuted: Boolean,
    @SerializedName("PositionTicks") val positionTicks: Long,
    @SerializedName("VolumeLevel") val volumeLevel: Int? = null,
    @SerializedName("AudioStreamIndex") val audioStreamIndex: Int? = null,
    @SerializedName("SubtitleStreamIndex") val subtitleStreamIndex: Int? = null,
    @SerializedName("PlayMethod") val playMethod: String,
    @SerializedName("LiveStreamId") val liveStreamId: String? = null,
    @SerializedName("PlaySessionId") val playSessionId: String,
    @SerializedName("EventName") val eventName: String? = null
)

data class PlaybackStopRequest(
    @SerializedName("ItemId") val itemId: String,
    @SerializedName("SessionId") val sessionId: String,
    @SerializedName("MediaSourceId") val mediaSourceId: String,
    @SerializedName("PositionTicks") val positionTicks: Long,
    @SerializedName("PlaySessionId") val playSessionId: String,
    @SerializedName("Failed") val failed: Boolean = false
)

data class PlaybackInfoResponse(
    @SerializedName("MediaSources") val mediaSources: List<MediaSource>,
    @SerializedName("PlaySessionId") val playSessionId: String,
    @SerializedName("ErrorCode") val errorCode: String? = null
)