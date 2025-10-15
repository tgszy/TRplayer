package com.trplayer.embyplayer.presentation.player

import android.content.Context
import android.net.Uri
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.DefaultMediaSourceFactory
import androidx.media3.exoplayer.trackselection.DefaultTrackSelector
import androidx.media3.exoplayer.upstream.DefaultHttpDataSource
import androidx.media3.exoplayer.upstream.cache.CacheDataSource
import androidx.media3.datasource.cache.LeastRecentlyUsedCacheEvictor
import com.trplayer.embyplayer.data.remote.EmbyApiService
import com.trplayer.embyplayer.domain.model.EmbyItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

/**
 * ExoPlayer管理器
 * 负责管理播放器实例、缓存和播放状态
 */
@Singleton
class ExoPlayerManager @Inject constructor(
    private val context: Context,
    private val apiService: EmbyApiService
) {
    
    private var exoPlayer: ExoPlayer? = null
    private var mediaCache: SimpleCache? = null
    
    private val _playerState = MutableStateFlow(PlayerState())
    val playerState: StateFlow<PlayerState> = _playerState.asStateFlow()
    
    companion object {
        private const val CACHE_DIR_NAME = "media_cache"
        private const val CACHE_SIZE = 100 * 1024 * 1024L // 100MB
    }
    
    /**
     * 初始化播放器
     */
    fun initializePlayer() {
        if (exoPlayer == null) {
            // 初始化缓存
            val cacheDir = File(context.cacheDir, CACHE_DIR_NAME)
            mediaCache = SimpleCache(cacheDir, LeastRecentlyUsedCacheEvictor(CACHE_SIZE))
            
            // 创建HTTP数据源工厂
            val httpDataSourceFactory = DefaultHttpDataSource.Factory()
                .setUserAgent("TRplayer/1.0")
                .setConnectTimeoutMs(DefaultHttpDataSource.DEFAULT_CONNECT_TIMEOUT_MILLIS)
                .setReadTimeoutMs(DefaultHttpDataSource.DEFAULT_READ_TIMEOUT_MILLIS)
            
            // 创建缓存数据源工厂
            val cacheDataSourceFactory = CacheDataSource.Factory()
                .setCache(mediaCache!!)
                .setUpstreamDataSourceFactory(httpDataSourceFactory)
                .setFlags(CacheDataSource.FLAG_IGNORE_CACHE_ON_ERROR)
            
            // 创建媒体源工厂
            val mediaSourceFactory = DefaultMediaSourceFactory(cacheDataSourceFactory)
            
            // 创建轨道选择器
            val trackSelector = DefaultTrackSelector(context).apply {
                setParameters(buildUponParameters().setMaxVideoSizeSd())
            }
            
            // 创建ExoPlayer实例
            exoPlayer = ExoPlayer.Builder(context)
                .setMediaSourceFactory(mediaSourceFactory)
                .setTrackSelector(trackSelector)
                .build()
                .apply {
                    // 设置播放器监听器
                    addListener(object : Player.Listener {
                        override fun onPlaybackStateChanged(playbackState: Int) {
                            _playerState.value = _playerState.value.copy(
                                playbackState = playbackState,
                                isPlaying = playbackState == Player.STATE_READY && isPlaying
                            )
                        }
                        
                        override fun onPlayerError(error: PlaybackException) {
                            _playerState.value = _playerState.value.copy(
                                error = error,
                                errorMessage = error.message ?: "播放错误"
                            )
                        }
                        
                        override fun onIsPlayingChanged(isPlaying: Boolean) {
                            _playerState.value = _playerState.value.copy(isPlaying = isPlaying)
                        }
                        
                        override fun onPositionDiscontinuity(
                            oldPosition: Player.PositionInfo,
                            newPosition: Player.PositionInfo,
                            reason: Int
                        ) {
                            _playerState.value = _playerState.value.copy(
                                currentPosition = newPosition.positionMs,
                                duration = duration
                            )
                        }
                    })
                }
        }
    }
    
    /**
     * 准备播放器
     */
    fun preparePlayer(mediaUrl: String) {
        initializePlayer()
        exoPlayer?.let { player ->
            try {
                // 创建媒体项
                val mediaItem = MediaItem.fromUri(Uri.parse(mediaUrl))
                
                // 设置媒体项并准备播放
                player.setMediaItem(mediaItem)
                player.prepare()
                
                _playerState.value = PlayerState(
                    isPlaying = false,
                    playbackState = Player.STATE_BUFFERING
                )
                
            } catch (e: Exception) {
                _playerState.value = _playerState.value.copy(
                    error = e,
                    errorMessage = "准备播放器失败: ${e.message}"
                )
            }
        }
    }
    
    /**
     * 播放媒体项
     */
    fun playMediaItem(embyItem: EmbyItem, accessToken: String) {
        exoPlayer?.let { player ->
            try {
                // 构建媒体URL（需要根据Emby API获取播放URL）
                val mediaUrl = buildMediaUrl(embyItem, accessToken)
                
                // 创建媒体项
                val mediaItem = MediaItem.fromUri(Uri.parse(mediaUrl))
                
                // 设置媒体项并准备播放
                player.setMediaItem(mediaItem)
                player.prepare()
                player.play()
                
                _playerState.value = PlayerState(
                    currentItem = embyItem,
                    isPlaying = true,
                    playbackState = Player.STATE_BUFFERING
                )
                
            } catch (e: Exception) {
                _playerState.value = _playerState.value.copy(
                    error = e,
                    errorMessage = "播放失败: ${e.message}"
                )
            }
        }
    }
    
    /**
     * 构建媒体播放URL
     */
    private fun buildMediaUrl(embyItem: EmbyItem, accessToken: String): String {
        // 这里需要根据Emby API构建播放URL
        // 实际实现需要调用Emby API获取播放信息
        val itemId = embyItem.id
        
        // 临时实现 - 实际需要调用Emby API获取播放URL
        return "https://your-emby-server.com/Videos/$itemId/stream?Static=true&api_key=$accessToken"
    }
    
    /**
     * 暂停播放
     */
    fun pause() {
        exoPlayer?.pause()
    }
    
    /**
     * 恢复播放
     */
    fun play() {
        exoPlayer?.play()
    }
    
    /**
     * 停止播放
     */
    fun stop() {
        exoPlayer?.stop()
        _playerState.value = PlayerState()
    }
    
    /**
     * 跳转到指定位置
     */
    fun seekTo(positionMs: Long) {
        exoPlayer?.seekTo(positionMs)
    }
    
    /**
     * 获取当前播放位置
     */
    fun getCurrentPosition(): Long {
        return exoPlayer?.currentPosition ?: 0L
    }
    
    /**
     * 获取媒体总时长
     */
    fun getDuration(): Long {
        return exoPlayer?.duration ?: 0L
    }
    
    /**
     * 设置音量
     */
    fun setVolume(volume: Float) {
        exoPlayer?.volume = volume.coerceIn(0f, 1f)
    }
    
    /**
     * 释放播放器资源
     */
    fun release() {
        exoPlayer?.release()
        exoPlayer = null
        mediaCache?.release()
        mediaCache = null
        _playerState.value = PlayerState()
    }
    
    /**
     * 获取播放器实例（用于UI绑定）
     */
    fun getPlayer(): ExoPlayer? = exoPlayer
}

/**
 * 播放器状态数据类
 */
data class PlayerState(
    val currentItem: EmbyItem? = null,
    val isPlaying: Boolean = false,
    val playbackState: Int = Player.STATE_IDLE,
    val currentPosition: Long = 0L,
    val duration: Long = 0L,
    val error: Throwable? = null,
    val errorMessage: String? = null
) {
    val isLoading: Boolean
        get() = playbackState == Player.STATE_BUFFERING
    
    val isReady: Boolean
        get() = playbackState == Player.STATE_READY
    
    val isEnded: Boolean
        get() = playbackState == Player.STATE_ENDED
    
    val progress: Float
        get() = if (duration > 0) (currentPosition.toFloat() / duration.toFloat()) else 0f
}