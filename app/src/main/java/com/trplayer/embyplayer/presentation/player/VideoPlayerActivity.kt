package com.trplayer.embyplayer.presentation.player

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.trplayer.embyplayer.presentation.theme.AdaptiveTheme

/**
 * 视频播放器Activity
 * 用于全屏视频播放
 */
class VideoPlayerActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        setContent {
            AdaptiveTheme(isTv = false) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    // TODO: 实现视频播放器界面
                    // VideoPlayerScreen()
                }
            }
        }
    }
}