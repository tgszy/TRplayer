package com.trplayer.embyplayer.presentation.components

import android.content.Context
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.exoplayer2.ui.PlayerView
import com.trplayer.embyplayer.presentation.player.ExoPlayerManager

/**
 * ExoPlayer Compose组件
 * 在Compose中嵌入ExoPlayer播放器视图
 */
@Composable
fun ExoPlayerComponent(
    exoPlayerManager: ExoPlayerManager,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    
    AndroidView(
        factory = { ctx: Context ->
            createPlayerView(ctx, exoPlayerManager)
        },
        modifier = modifier,
        update = { playerView: PlayerView ->
            // 更新播放器视图
            exoPlayerManager.getPlayer()?.let { player ->
                playerView.player = player
            }
        }
    )
}

/**
 * 创建播放器视图
 */
private fun createPlayerView(context: Context, exoPlayerManager: ExoPlayerManager): PlayerView {
    return PlayerView(context).apply {
        useController = true
        setShowBuffering(PlayerView.SHOW_BUFFERING_ALWAYS)
        setShowFastForwardButton(true)
        setShowRewindButton(true)
        setShowNextButton(false)
        setShowPreviousButton(false)
        
        // 设置播放器
        exoPlayerManager.getPlayer()?.let { player ->
            this.player = player
        }
    }
}