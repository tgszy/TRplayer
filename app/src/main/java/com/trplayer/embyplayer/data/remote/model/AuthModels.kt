package com.trplayer.embyplayer.data.remote.model

import com.google.gson.annotations.SerializedName

data class EmbyUser(
    @SerializedName("Id") val id: String,
    @SerializedName("Name") val name: String,
    @SerializedName("ServerId") val serverId: String,
    @SerializedName("HasPassword") val hasPassword: Boolean,
    @SerializedName("HasConfiguredPassword") val hasConfiguredPassword: Boolean,
    @SerializedName("HasConfiguredEasyPassword") val hasConfiguredEasyPassword: Boolean,
    @SerializedName("EnableAutoLogin") val enableAutoLogin: Boolean,
    @SerializedName("LastLoginDate") val lastLoginDate: String?,
    @SerializedName("LastActivityDate") val lastActivityDate: String?
)

data class AuthenticationResult(
    @SerializedName("User") val user: EmbyUser,
    @SerializedName("SessionInfo") val sessionInfo: SessionInfo,
    @SerializedName("AccessToken") val accessToken: String,
    @SerializedName("ServerId") val serverId: String
)

data class SessionInfo(
    @SerializedName("Id") val id: String,
    @SerializedName("UserId") val userId: String,
    @SerializedName("UserName") val userName: String,
    @SerializedName("Client") val client: String,
    @SerializedName("DeviceId") val deviceId: String,
    @SerializedName("DeviceName") val deviceName: String,
    @SerializedName("RemoteEndPoint") val remoteEndPoint: String,
    @SerializedName("PlayState") val playState: PlayState?
)

data class PlayState(
    @SerializedName("PositionTicks") val positionTicks: Long?,
    @SerializedName("CanSeek") val canSeek: Boolean,
    @SerializedName("IsPaused") val isPaused: Boolean,
    @SerializedName("IsMuted") val isMuted: Boolean,
    @SerializedName("VolumeLevel") val volumeLevel: Int?
)