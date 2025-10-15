package com.trplayer.embyplayer.data.model

import com.trplayer.embyplayer.domain.model.EmbyItem
import com.trplayer.embyplayer.domain.model.MediaSource
import com.trplayer.embyplayer.domain.model.UserData

/**
 * 媒体项数据模型
 * 表示应用中的媒体内容，支持strm文件格式
 */
data class MediaItem(
    val id: String,
    val title: String,
    val subtitle: String = "",
    val description: String = "",
    val year: Int = 0,
    val duration: Int = 0,
    val type: MediaType,
    val thumbnailUrl: String = "",
    val backdropUrl: String = "",
    val playbackProgress: Float? = null,
    val isFavorite: Boolean = false,
    val isPlayed: Boolean = false,
    val filePath: String = "",
    val fileFormat: FileFormat = FileFormat.UNKNOWN,
    val isStrmFile: Boolean = false,
    val strmTargetPath: String = "",
    val mediaSources: List<MediaSource> = emptyList(),
    val userData: UserData? = null
) {
    /**
     * 从EmbyItem转换到MediaItem
     */
    companion object {
        fun fromEmbyItem(embyItem: EmbyItem, serverBaseUrl: String): MediaItem {
            // 检测是否为strm文件
            val isStrm = embyItem.mediaSources?.any { it.path.endsWith(".strm", ignoreCase = true) } == true
            val strmTargetPath = if (isStrm) {
                embyItem.mediaSources?.firstOrNull()?.path ?: ""
            } else ""
            
            // 检测文件格式
            val fileFormat = detectFileFormat(embyItem.mediaSources?.firstOrNull()?.path ?: "")
            
            return MediaItem(
                id = embyItem.id,
                title = embyItem.displayName,
                description = embyItem.overview ?: "",
                year = embyItem.productionYear ?: 0,
                duration = (embyItem.durationSeconds ?: 0).toInt(),
                type = detectMediaType(embyItem.type, embyItem.mediaType),
                thumbnailUrl = buildImageUrl(embyItem, serverBaseUrl, "Primary"),
                backdropUrl = buildImageUrl(embyItem, serverBaseUrl, "Backdrop"),
                isFavorite = embyItem.userData?.isFavorite ?: false,
                isPlayed = embyItem.userData?.played ?: false,
                playbackProgress = embyItem.userData?.playedPercentage?.toFloat(),
                filePath = embyItem.mediaSources?.firstOrNull()?.path ?: "",
                fileFormat = fileFormat,
                isStrmFile = isStrm,
                strmTargetPath = strmTargetPath,
                mediaSources = embyItem.mediaSources ?: emptyList(),
                userData = embyItem.userData
            )
        }
        
        private fun detectFileFormat(filePath: String): FileFormat {
            return when {
                filePath.endsWith(".strm", ignoreCase = true) -> FileFormat.STRM
                filePath.endsWith(".mp4", ignoreCase = true) -> FileFormat.MP4
                filePath.endsWith(".mkv", ignoreCase = true) -> FileFormat.MKV
                filePath.endsWith(".avi", ignoreCase = true) -> FileFormat.AVI
                filePath.endsWith(".mov", ignoreCase = true) -> FileFormat.MOV
                filePath.endsWith(".wmv", ignoreCase = true) -> FileFormat.WMV
                filePath.endsWith(".flv", ignoreCase = true) -> FileFormat.FLV
                filePath.endsWith(".webm", ignoreCase = true) -> FileFormat.WEBM
                filePath.endsWith(".m4v", ignoreCase = true) -> FileFormat.M4V
                filePath.endsWith(".ts", ignoreCase = true) -> FileFormat.TS
                filePath.endsWith(".m2ts", ignoreCase = true) -> FileFormat.M2TS
                else -> FileFormat.UNKNOWN
            }
        }
        
        private fun detectMediaType(type: String, mediaType: String?): MediaType {
            return when {
                type == "Movie" || mediaType == "Video" -> MediaType.MOVIE
                type == "Series" -> MediaType.TV_SHOW
                type == "Episode" -> MediaType.EPISODE
                type == "MusicAlbum" -> MediaType.ALBUM
                type == "Audio" || mediaType == "Audio" -> MediaType.MUSIC
                type == "Photo" || mediaType == "Photo" -> MediaType.PHOTO
                else -> MediaType.UNKNOWN
            }
        }
        
        private fun buildImageUrl(embyItem: EmbyItem, serverBaseUrl: String, imageType: String): String {
            val imageTag = when (imageType) {
                "Primary" -> embyItem.imageTags?.get("Primary")
                "Backdrop" -> embyItem.backdropImageTags?.firstOrNull()
                else -> null
            }
            
            return if (imageTag != null) {
                "$serverBaseUrl/Items/${embyItem.id}/Images/$imageType?tag=$imageTag"
            } else ""
        }
    }
}

/**
 * 媒体类型枚举
 */
enum class MediaType {
    MOVIE,
    TV_SHOW,
    EPISODE,
    MUSIC,
    ALBUM,
    SONG,
    PHOTO,
    UNKNOWN
}

/**
 * 文件格式枚举
 */
enum class FileFormat {
    STRM,
    MP4,
    MKV,
    AVI,
    MOV,
    WMV,
    FLV,
    WEBM,
    M4V,
    TS,
    M2TS,
    UNKNOWN
}

/**
 * 媒体源数据模型
 */
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
    val supportsDirectPlay: Boolean
)

/**
 * 用户数据模型
 */
data class UserData(
    val rating: Double?,
    val playedPercentage: Double?,
    val playbackPositionTicks: Long?,
    val playCount: Int,
    val isFavorite: Boolean,
    val lastPlayedDate: String?,
    val played: Boolean
)