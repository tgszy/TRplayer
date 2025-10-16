package com.trplayer.embyplayer.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.trplayer.embyplayer.data.remote.api.PlaybackInfoResponse
import com.trplayer.embyplayer.data.remote.api.PlaybackStartRequest
import com.trplayer.embyplayer.data.remote.api.PlaybackProgressRequest
import com.trplayer.embyplayer.data.remote.api.PlaybackStopRequest
import com.trplayer.embyplayer.domain.model.EmbyItem
import com.trplayer.embyplayer.domain.model.EmbyLibrary
import com.trplayer.embyplayer.domain.model.EmbyServer
import com.trplayer.embyplayer.domain.repository.EmbyRepository
import com.trplayer.embyplayer.presentation.player.ExoPlayerManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * 服务器管理ViewModel
 * 负责管理Emby服务器连接和配置
 */
@HiltViewModel
class ServerViewModel @Inject constructor(
    private val repository: EmbyRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ServerUiState())
    val uiState: StateFlow<ServerUiState> = _uiState.asStateFlow()

    /**
     * 添加新的服务器配置
     */
    fun addServer(server: EmbyServer) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            
            try {
                // 获取当前服务器列表
                val currentServers = repository.getServers()
                val updatedServers = currentServers + server
                
                // 保存更新后的服务器列表
                repository.setServers(updatedServers)
                
                _uiState.update { state ->
                    state.copy(
                        servers = updatedServers,
                        isLoading = false,
                        errorMessage = null
                    )
                }
            } catch (e: Exception) {
                _uiState.update { state ->
                    state.copy(
                        isLoading = false,
                        errorMessage = "添加服务器失败: ${e.message}"
                    )
                }
            }
        }
    }

    /**
     * 连接到指定的服务器
     */
    fun connectToServer(server: EmbyServer) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            
            try {
                // 设置当前服务器
                repository.setCurrentServer(server)
                
                _uiState.update { state ->
                    state.copy(
                        currentServer = server,
                        isLoading = false,
                        errorMessage = null
                    )
                }
            } catch (e: Exception) {
                _uiState.update { state ->
                    state.copy(
                        isLoading = false,
                        errorMessage = "连接服务器失败: ${e.message}"
                    )
                }
            }
        }
    }

    /**
     * 加载服务器列表
     */
    fun loadServers() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            
            try {
                val servers = repository.getServers()
                val currentServer = repository.getCurrentServer().first()
                
                _uiState.update { state ->
                    state.copy(
                        servers = servers,
                        currentServer = currentServer,
                        isLoading = false,
                        errorMessage = null
                    )
                }
            } catch (e: Exception) {
                _uiState.update { state ->
                    state.copy(
                        isLoading = false,
                        errorMessage = "加载服务器列表失败: ${e.message}"
                    )
                }
            }
        }
    }

    /**
     * 清除错误消息
     */
    fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }
}

/**
 * 服务器管理UI状态
 */
data class ServerUiState(
    val servers: List<EmbyServer> = emptyList(),
    val currentServer: EmbyServer? = null,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

/**
 * 媒体库浏览ViewModel
 * 负责管理媒体库和媒体项的浏览
 */
@HiltViewModel
class LibraryViewModel @Inject constructor(
    private val repository: EmbyRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(LibraryUiState())
    val uiState: StateFlow<LibraryUiState> = _uiState.asStateFlow()

    /**
     * 加载用户媒体库
     */
    fun loadLibraries(userId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            
            try {
                val result = repository.getUserLibraries(userId)
                if (result.isSuccess) {
                    _uiState.update { state ->
                        state.copy(
                            libraries = result.getOrThrow(),
                            isLoading = false,
                            errorMessage = null
                        )
                    }
                } else {
                    _uiState.update { state ->
                        state.copy(
                            isLoading = false,
                            errorMessage = "加载媒体库失败: ${result.exceptionOrNull()?.message}"
                        )
                    }
                }
            } catch (e: Exception) {
                _uiState.update { state ->
                    state.copy(
                        isLoading = false,
                        errorMessage = "加载媒体库失败: ${e.message}"
                    )
                }
            }
        }
    }

    /**
     * 加载媒体库中的媒体项
     */
    fun loadLibraryItems(userId: String, libraryId: String, itemTypes: List<String>? = null) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            
            try {
                val result = repository.getLibraryItems(
                    userId = userId,
                    parentId = libraryId,
                    itemTypes = itemTypes
                )
                if (result.isSuccess) {
                    _uiState.update { state ->
                        state.copy(
                            currentItems = result.getOrThrow(),
                            isLoading = false,
                            errorMessage = null
                        )
                    }
                } else {
                    _uiState.update { state ->
                        state.copy(
                            isLoading = false,
                            errorMessage = "加载媒体项失败: ${result.exceptionOrNull()?.message}"
                        )
                    }
                }
            } catch (e: Exception) {
                _uiState.update { state ->
                    state.copy(
                        isLoading = false,
                        errorMessage = "加载媒体项失败: ${e.message}"
                    )
                }
            }
        }
    }

    /**
     * 搜索媒体项
     */
    fun searchItems(userId: String, searchTerm: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            
            try {
                val result = repository.searchItems(userId, searchTerm, 20)
                if (result.isSuccess) {
                    _uiState.update { state ->
                        state.copy(
                            searchResults = result.getOrThrow(),
                            isLoading = false,
                            errorMessage = null
                        )
                    }
                } else {
                    _uiState.update { state ->
                        state.copy(
                            isLoading = false,
                            errorMessage = "搜索失败: ${result.exceptionOrNull()?.message}"
                        )
                    }
                }
            } catch (e: Exception) {
                _uiState.update { state ->
                    state.copy(
                        isLoading = false,
                        errorMessage = "搜索失败: ${e.message}"
                    )
                }
            }
        }
    }

    /**
     * 清除错误消息
     */
    fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }

    /**
     * 清除搜索结果
     */
    fun clearSearch() {
        _uiState.update { it.copy(searchResults = emptyList()) }
    }
}

/**
 * 媒体库浏览UI状态
 */
data class LibraryUiState(
    val libraries: List<EmbyLibrary> = emptyList(),
    val currentItems: List<EmbyItem> = emptyList(),
    val searchResults: List<EmbyItem> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

/**
 * 播放器ViewModel
 * 负责管理媒体播放和播放控制
 */
@HiltViewModel
class PlayerViewModel @Inject constructor(
    private val repository: EmbyRepository,
    val exoPlayerManager: ExoPlayerManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(PlayerUiState())
    val uiState: StateFlow<PlayerUiState> = _uiState.asStateFlow()

    /**
     * 准备播放媒体项
     */
    fun preparePlayback(userId: String, itemId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            
            try {
                // 获取媒体项详细信息
                val itemResult = repository.getItemDetails(userId, itemId)
                if (itemResult.isSuccess) {
                    val item = itemResult.getOrThrow()
                    
                    // 获取播放信息
                    val playbackResult = repository.getPlaybackInfo(userId, itemId)
                    if (playbackResult.isSuccess) {
                        val playbackInfo = playbackResult.getOrThrow()
                        
                        // 获取播放URL
                        val mediaSource = playbackInfo.mediaSources?.firstOrNull()
                        if (mediaSource != null) {
                            val mediaUrlResult = repository.getPlaybackUrl(
                                userId = userId,
                                itemId = itemId,
                                mediaSourceId = mediaSource.id
                            )
                            
                            if (mediaUrlResult.isSuccess) {
                                val mediaUrl = mediaUrlResult.getOrThrow()
                                exoPlayerManager.preparePlayer(mediaUrl)
                                
                                _uiState.update { state ->
                                    state.copy(
                                        currentItem = item,
                                        playbackInfo = playbackInfo,
                                        isLoading = false,
                                        errorMessage = null
                                    )
                                }
                                
                                // 开始播放
                                play()
                            } else {
                                _uiState.update { state ->
                                    state.copy(
                                        isLoading = false,
                                        errorMessage = "无法获取播放地址: ${mediaUrlResult.exceptionOrNull()?.message}"
                                    )
                                }
                            }
                        } else {
                            _uiState.update { state ->
                                state.copy(
                                    isLoading = false,
                                    errorMessage = "无法获取播放地址"
                                )
                            }
                        }
                    } else {
                        _uiState.update { state ->
                            state.copy(
                                isLoading = false,
                                errorMessage = "获取播放信息失败"
                            )
                        }
                    }
                } else {
                    _uiState.update { state ->
                        state.copy(
                            isLoading = false,
                            errorMessage = "获取媒体项信息失败"
                        )
                    }
                }
            } catch (e: Exception) {
                _uiState.update { state ->
                    state.copy(
                        isLoading = false,
                        errorMessage = "准备播放失败: ${e.message}"
                    )
                }
            }
        }
    }
    
    /**
     * 开始播放
     */
    fun play() {
        viewModelScope.launch {
            exoPlayerManager.play()
            _uiState.update { it.copy(isPlaying = true) }
        }
    }
    
    /**
     * 暂停播放
     */
    fun pause() {
        viewModelScope.launch {
            exoPlayerManager.pause()
            _uiState.update { it.copy(isPlaying = false) }
        }
    }
    
    /**
     * 跳转到指定位置
     */
    fun seekTo(position: Long) {
        viewModelScope.launch {
            exoPlayerManager.seekTo(position)
            _uiState.update { it.copy(currentPosition = position) }
        }
    }
    
    /**
     * 设置音量
     */
    fun setVolume(volume: Float) {
        viewModelScope.launch {
            exoPlayerManager.setVolume(volume)
            _uiState.update { it.copy(volume = volume) }
        }
    }

    /**
     * 报告播放开始
     */
    fun reportPlaybackStart(request: PlaybackStartRequest) {
        viewModelScope.launch {
            try {
                repository.reportPlaybackStart(request)
            } catch (e: Exception) {
                // 报告失败不影响播放，只记录错误
                println("报告播放开始失败: ${e.message}")
            }
        }
    }

    /**
     * 报告播放进度
     */
    fun reportPlaybackProgress(request: PlaybackProgressRequest) {
        viewModelScope.launch {
            try {
                repository.reportPlaybackProgress(request)
            } catch (e: Exception) {
                // 报告失败不影响播放，只记录错误
                println("报告播放进度失败: ${e.message}")
            }
        }
    }

    /**
     * 报告播放停止
     */
    fun reportPlaybackStopped(request: PlaybackStopRequest) {
        viewModelScope.launch {
            try {
                repository.reportPlaybackStopped(request)
            } catch (e: Exception) {
                // 报告失败不影响播放，只记录错误
                println("报告播放停止失败: ${e.message}")
            }
        }
    }

    /**
     * 清除错误消息
     */
    fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }
}

/**
 * 播放器UI状态
 */
data class PlayerUiState(
    val currentItem: EmbyItem? = null,
    val playbackInfo: PlaybackInfoResponse? = null,
    val isLoading: Boolean = false,
    val isPlaying: Boolean = false,
    val currentPosition: Long = 0L,
    val volume: Float = 1.0f,
    val errorMessage: String? = null
)