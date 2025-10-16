package com.trplayer.embyplayer.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class EmbyServer(
    val id: String,
    val name: String,
    val scheme: String,
    val host: String,
    val port: Int,
    val path: String = "",
    val userId: String? = null,
    val accessToken: String? = null,
    val isActive: Boolean = false
) {
    fun getBaseUrl(): String {
        return "$scheme://$host:$port${if (path.isNotEmpty()) "/$path" else ""}"
    }
    
    fun getEmbyUrl(): String {
        return "${getBaseUrl()}/emby"
    }
}

data class EmbyUser(
    val id: String,
    val name: String,
    val serverId: String,
    val hasPassword: Boolean,
    val lastLoginDate: String?
)

data class EmbyLibrary(
    val id: String,
    val name: String,
    val type: String,
    val collectionType: String?,
    val itemCount: Int = 0
)

data class EmbyItem(
    val id: String,
    val name: String,
    val type: String,
    val mediaType: String?,
    val overview: String?,
    val productionYear: Int?,
    val premiereDate: String?,
    val runtimeTicks: Long?,
    val seriesName: String?,
    val seasonName: String?,
    val episodeNumber: Int?,
    val seasonNumber: Int?,
    val communityRating: Double?,
    val officialRating: String?,
    val imageTags: Map<String, String>?,
    val backdropImageTags: List<String>?,
    val mediaSources: List<MediaSource>?,
    val chapters: List<Chapter>?,
    val userData: UserData?
) {
    val duration: Long?
        get() = runtimeTicks?.let { it / 10000 } // Convert ticks to milliseconds
    
    val durationSeconds: Long?
        get() = duration?.let { it / 1000 }
    
    val displayName: String
        get() = when (type) {
            "Episode" -> {
                val season = seasonNumber?.let { "S${it.toString().padStart(2, '0')}" } ?: ""
                val episode = episodeNumber?.let { "E${it.toString().padStart(2, '0')}" } ?: ""
                "$seriesName $season$episode - $name"
            }
            else -> name
        }
}

data class MediaSource(
    val id: String,
    val path: String,
    val protocol: String,
    val container: String,
    val size: Long,
    val name: String,
    val isRemote: Boolean,
    val runtimeTicks: Long,
    val supportsTranscoding: Boolean,
    val supportsDirectStream: Boolean,
    val supportsDirectPlay: Boolean,
    val videoStream: VideoStream?,
    val audioStream: AudioStream?,
    val mediaStreams: List<MediaStream>?
) {
    val runtime: Long
        get() = runtimeTicks / 10000 // Convert ticks to milliseconds
}

data class MediaStream(
    val index: Int,
    val type: String, // Video, Audio, Subtitle
    val codec: String,
    val language: String?,
    val title: String?,
    val displayTitle: String?,
    val isDefault: Boolean,
    val isForced: Boolean,
    val isHearingImpaired: Boolean,
    // Video specific
    val width: Int?,
    val height: Int?,
    val aspectRatio: String?,
    val averageFrameRate: Float?,
    val realFrameRate: Float?,
    val profile: String?,
    val level: Double?,
    // Audio specific
    val channels: Int?,
    val sampleRate: Int?,
    val bitrate: Int?,
    val bitDepth: Int?
) {
    val isVideo: Boolean get() = type == "Video"
    val isAudio: Boolean get() = type == "Audio"
    val isSubtitle: Boolean get() = type == "Subtitle"
}

data class VideoStream(
    val codec: String,
    val width: Int,
    val height: Int,
    val averageFrameRate: Float,
    val realFrameRate: Float,
    val profile: String,
    val level: Double,
    val pixelFormat: String,
    val refFrames: Int
)

data class AudioStream(
    val codec: String,
    val channels: Int,
    val sampleRate: Int,
    val bitrate: Int
)

data class Chapter(
    val startPositionTicks: Long,
    val name: String,
    val imagePath: String?
) {
    val startPositionMs: Long
        get() = startPositionTicks / 10000
}

data class UserData(
    val rating: Double?,
    val playedPercentage: Double?,
    val unplayedItemCount: Int?,
    val playbackPositionTicks: Long?,
    val playCount: Int,
    val isFavorite: Boolean,
    val likes: Boolean?,
    val lastPlayedDate: String?,
    val played: Boolean,
    val key: String
) {
    val playbackPositionMs: Long?
        get() = playbackPositionTicks?.let { it / 10000 }
}