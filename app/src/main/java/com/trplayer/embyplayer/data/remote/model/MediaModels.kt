package com.trplayer.embyplayer.data.remote.model

import com.google.gson.annotations.SerializedName

data class EmbyLibrary(
    @SerializedName("Name") val name: String,
    @SerializedName("ServerId") val serverId: String,
    @SerializedName("Id") val id: String,
    @SerializedName("Etag") val etag: String,
    @SerializedName("CanDelete") val canDelete: Boolean,
    @SerializedName("CanDownload") val canDownload: Boolean,
    @SerializedName("SupportsSync") val supportsSync: Boolean,
    @SerializedName("SupportsUpload") val supportsUpload: Boolean,
    @SerializedName("IsPublic") val isPublic: Boolean,
    @SerializedName("ChannelId") val channelId: String?,
    @SerializedName("FolderType") val folderType: String,
    @SerializedName("Type") val type: String,
    @SerializedName("CollectionType") val collectionType: String?
)

data class EmbyItem(
    @SerializedName("Name") val name: String,
    @SerializedName("ServerId") val serverId: String,
    @SerializedName("Id") val id: String,
    @SerializedName("Etag") val etag: String,
    @SerializedName("Type") val type: String,
    @SerializedName("MediaType") val mediaType: String?,
    @SerializedName("ChannelId") val channelId: String?,
    @SerializedName("CommunityRating") val communityRating: Double?,
    @SerializedName("ProductionYear") val productionYear: Int?,
    @SerializedName("PremiereDate") val premiereDate: String?,
    @SerializedName("Overview") val overview: String?,
    @SerializedName("Path") val path: String?,
    @SerializedName("OfficialRating") val officialRating: String?,
    @SerializedName("RunTimeTicks") val runTimeTicks: Long?,
    @SerializedName("Size") val size: Long?,
    @SerializedName("Container") val container: String?,
    @SerializedName("VideoType") val videoType: String?,
    @SerializedName("ImageTags") val imageTags: Map<String, String>?,
    @SerializedName("BackdropImageTags") val backdropImageTags: List<String>?,
    @SerializedName("SeriesName") val seriesName: String?,
    @SerializedName("SeriesId") val seriesId: String?,
    @SerializedName("SeasonName") val seasonName: String?,
    @SerializedName("SeasonId") val seasonId: String?,
    @SerializedName("IndexNumber") val indexNumber: Int?,
    @SerializedName("ParentIndexNumber") val parentIndexNumber: Int?,
    @SerializedName("MediaSources") val mediaSources: List<MediaSource>?,
    @SerializedName("Chapters") val chapters: List<Chapter>?,
    @SerializedName("Trickplay") val trickplay: List<TrickplayInfo>?,
    @SerializedName("UserData") val userData: UserData?
)

data class MediaSource(
    @SerializedName("Protocol") val protocol: String,
    @SerializedName("Id") val id: String,
    @SerializedName("Path") val path: String,
    @SerializedName("Type") val type: String,
    @SerializedName("Container") val container: String,
    @SerializedName("Size") val size: Long,
    @SerializedName("Name") val name: String,
    @SerializedName("IsRemote") val isRemote: Boolean,
    @SerializedName("RunTimeTicks") val runTimeTicks: Long,
    @SerializedName("SupportsTranscoding") val supportsTranscoding: Boolean,
    @SerializedName("SupportsDirectStream") val supportsDirectStream: Boolean,
    @SerializedName("SupportsDirectPlay") val supportsDirectPlay: Boolean,
    @SerializedName("IsInfiniteStream") val isInfiniteStream: Boolean,
    @SerializedName("RequiresOpening") val requiresOpening: Boolean,
    @SerializedName("RequiresClosing") val requiresClosing: Boolean,
    @SerializedName("RequiresLooping") val requiresLooping: Boolean,
    @SerializedName("SupportsProbing") val supportsProbing: Boolean,
    @SerializedName("VideoStream") val videoStream: VideoStream?,
    @SerializedName("AudioStream") val audioStream: AudioStream?,
    @SerializedName("MediaStreams") val mediaStreams: List<MediaStream>?
)

data class MediaStream(
    @SerializedName("Codec") val codec: String,
    @SerializedName("CodecTag") val codecTag: String?,
    @SerializedName("Language") val language: String?,
    @SerializedName("TimeBase") val timeBase: String?,
    @SerializedName("CodecTimeBase") val codecTimeBase: String?,
    @SerializedName("Title") val title: String?,
    @SerializedName("DisplayTitle") val displayTitle: String?,
    @SerializedName("IsInterlaced") val isInterlaced: Boolean,
    @SerializedName("IsDefault") val isDefault: Boolean,
    @SerializedName("IsForced") val isForced: Boolean,
    @SerializedName("IsHearingImpaired") val isHearingImpaired: Boolean,
    @SerializedName("Type") val type: String,
    @SerializedName("Index") val index: Int,
    @SerializedName("Bitrate") val bitrate: Int?,
    @SerializedName("Channels") val channels: Int?,
    @SerializedName("SampleRate") val sampleRate: Int?,
    @SerializedName("BitDepth") val bitDepth: Int?,
    @SerializedName("Width") val width: Int?,
    @SerializedName("Height") val height: Int?,
    @SerializedName("AspectRatio") val aspectRatio: String?,
    @SerializedName("AverageFrameRate") val averageFrameRate: Float?,
    @SerializedName("RealFrameRate") val realFrameRate: Float?,
    @SerializedName("Profile") val profile: String?,
    @SerializedName("Level") val level: Double?,
    @SerializedName("PixelFormat") val pixelFormat: String?,
    @SerializedName("RefFrames") val refFrames: Int?
)

data class VideoStream(
    @SerializedName("Codec") val codec: String,
    @SerializedName("Width") val width: Int,
    @SerializedName("Height") val height: Int,
    @SerializedName("AverageFrameRate") val averageFrameRate: Float,
    @SerializedName("RealFrameRate") val realFrameRate: Float,
    @SerializedName("Profile") val profile: String,
    @SerializedName("Level") val level: Double,
    @SerializedName("PixelFormat") val pixelFormat: String,
    @SerializedName("RefFrames") val refFrames: Int
)

data class AudioStream(
    @SerializedName("Codec") val codec: String,
    @SerializedName("Channels") val channels: Int,
    @SerializedName("SampleRate") val sampleRate: Int,
    @SerializedName("Bitrate") val bitrate: Int
)

data class Chapter(
    @SerializedName("StartPositionTicks") val startPositionTicks: Long,
    @SerializedName("Name") val name: String,
    @SerializedName("ImagePath") val imagePath: String?,
    @SerializedName("ImageDateModified") val imageDateModified: Long?
)

data class TrickplayInfo(
    @SerializedName("Width") val width: Int,
    @SerializedName("Height") val height: Int,
    @SerializedName("TileWidth") val tileWidth: Int,
    @SerializedName("TileHeight") val tileHeight: Int,
    @SerializedName("ThumbnailCount") val thumbnailCount: Int,
    @SerializedName("Interval") val interval: Int,
    @SerializedName("Bandwidth") val bandwidth: Int
)

data class UserData(
    @SerializedName("Rating") val rating: Double?,
    @SerializedName("PlayedPercentage") val playedPercentage: Double?,
    @SerializedName("UnplayedItemCount") val unplayedItemCount: Int?,
    @SerializedName("PlaybackPositionTicks") val playbackPositionTicks: Long?,
    @SerializedName("PlayCount") val playCount: Int,
    @SerializedName("IsFavorite") val isFavorite: Boolean,
    @SerializedName("Likes") val likes: Boolean?,
    @SerializedName("LastPlayedDate") val lastPlayedDate: String?,
    @SerializedName("Played") val played: Boolean,
    @SerializedName("Key") val key: String
)

data class PlaybackInfoResponse(
    @SerializedName("MediaSources") val mediaSources: List<MediaSource>?,
    @SerializedName("PlaySessionId") val playSessionId: String?,
    @SerializedName("ErrorCode") val errorCode: String?
)

data class PlaybackStartRequest(
    @SerializedName("CanSeek") val canSeek: Boolean,
    @SerializedName("ItemId") val itemId: String,
    @SerializedName("MediaSourceId") val mediaSourceId: String,
    @SerializedName("AudioStreamIndex") val audioStreamIndex: Int?,
    @SerializedName("SubtitleStreamIndex") val subtitleStreamIndex: Int?,
    @SerializedName("PlayMethod") val playMethod: String,
    @SerializedName("PlaySessionId") val playSessionId: String
)

data class PlaybackProgressRequest(
    @SerializedName("ItemId") val itemId: String,
    @SerializedName("MediaSourceId") val mediaSourceId: String,
    @SerializedName("PlaySessionId") val playSessionId: String,
    @SerializedName("PositionTicks") val positionTicks: Long,
    @SerializedName("IsPaused") val isPaused: Boolean,
    @SerializedName("PlayMethod") val playMethod: String,
    @SerializedName("CanSeek") val canSeek: Boolean,
    @SerializedName("AudioStreamIndex") val audioStreamIndex: Int?,
    @SerializedName("SubtitleStreamIndex") val subtitleStreamIndex: Int?
)

data class PlaybackStopRequest(
    @SerializedName("ItemId") val itemId: String,
    @SerializedName("MediaSourceId") val mediaSourceId: String,
    @SerializedName("PlaySessionId") val playSessionId: String,
    @SerializedName("PositionTicks") val positionTicks: Long,
    @SerializedName("PlayMethod") val playMethod: String,
    @SerializedName("Failed") val failed: Boolean = false
)