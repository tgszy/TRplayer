package com.trplayer.embyplayer.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.trplayer.embyplayer.presentation.components.ExoPlayerComponent
import com.trplayer.embyplayer.presentation.viewmodels.PlayerViewModel

/**
 * 播放器界面
 * 提供视频播放控制和播放信息显示
 */
@Composable
fun PlayerScreen(
    navController: NavHostController,
    itemId: String? = null,
    userId: String? = null
) {
    val playerViewModel: PlayerViewModel = hiltViewModel()
    
    // 初始化播放器
    LaunchedEffect(Unit) {
        if (itemId != null && userId != null) {
            playerViewModel.preparePlayback(userId, itemId)
        }
    }
    
    val uiState by playerViewModel.uiState.collectAsState()
    var showControls by remember { mutableStateOf(false) }
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        // 视频播放区域
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clickable { showControls = !showControls }
        ) {
            if (uiState.currentItem != null && uiState.playbackInfo != null) {
                // 使用实际的ExoPlayer播放器
                ExoPlayerComponent(
                    exoPlayerManager = playerViewModel.exoPlayerManager,
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                // 加载状态或错误状态
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .align(Alignment.Center),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    if (uiState.isLoading) {
                        CircularProgressIndicator(color = Color.White)
                        Text(
                            text = "正在加载...",
                            style = MaterialTheme.typography.titleLarge,
                            color = Color.White,
                            modifier = Modifier.padding(top = 16.dp)
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = "播放",
                            modifier = Modifier.size(64.dp),
                            tint = Color.White
                        )
                        Text(
                            text = uiState.errorMessage ?: "准备播放",
                            style = MaterialTheme.typography.titleLarge,
                            color = Color.White,
                            modifier = Modifier.padding(top = 16.dp)
                        )
                    }
                }
            }
        }
        
        // 播放控制面板
        if (showControls) {
            PlayerControls(
                isPlaying = uiState.isPlaying,
                currentPosition = uiState.currentPosition,
                totalDuration = uiState.currentItem?.runtimeTicks?.div(10000) ?: 0L,
                onPlayPause = { 
                    if (uiState.isPlaying) {
                        playerViewModel.pause()
                    } else {
                        playerViewModel.play()
                    }
                },
                onSeek = { position -> playerViewModel.seekTo(position) },
                onBack = { navController.popBackStack() },
                modifier = Modifier.align(Alignment.BottomCenter)
            )
        }
        
        // 顶部标题栏
        PlayerTopBar(
            title = "当前播放的视频",
            onBack = { navController.popBackStack() },
            modifier = Modifier.align(Alignment.TopCenter)
        )
    }
}

/**
 * 播放器顶部栏
 */
@Composable
fun PlayerTopBar(
    title: String,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp)
            .background(Color.Black.copy(alpha = 0.7f))
    ) {
        // 返回按钮
        IconButton(
            onClick = onBack,
            modifier = Modifier
                .align(Alignment.CenterStart)
                .padding(start = 16.dp)
        ) {
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = "返回",
                tint = Color.White
            )
        }
        
        // 标题
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            color = Color.White,
            modifier = Modifier.align(Alignment.Center)
        )
    }
}

/**
 * 播放器控制面板
 */
@Composable
fun PlayerControls(
    isPlaying: Boolean,
    currentPosition: Long,
    totalDuration: Long,
    onPlayPause: () -> Unit,
    onSeek: (Long) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(Color.Black.copy(alpha = 0.7f))
            .padding(16.dp)
    ) {
        // 进度条
        PlayerProgressBar(
            currentPosition = currentPosition,
            totalDuration = totalDuration,
            onSeek = onSeek
        )
        
        // 时间显示
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = formatTime(currentPosition),
                style = MaterialTheme.typography.bodySmall,
                color = Color.White
            )
            Text(
                text = formatTime(totalDuration),
                style = MaterialTheme.typography.bodySmall,
                color = Color.White
            )
        }
        
        // 控制按钮
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 后退10秒
            IconButton(onClick = { /* 后退10秒 */ }) {
                Icon(
                    imageVector = Icons.Default.Replay10,
                    contentDescription = "后退10秒",
                    tint = Color.White
                )
            }
            
            // 播放/暂停
            IconButton(
                onClick = onPlayPause,
                modifier = Modifier.size(48.dp)
            ) {
                Icon(
                    imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                    contentDescription = if (isPlaying) "暂停" else "播放",
                    tint = Color.White,
                    modifier = Modifier.size(32.dp)
                )
            }
            
            // 前进10秒
            IconButton(onClick = { /* 前进10秒 */ }) {
                Icon(
                    imageVector = Icons.Default.Forward10,
                    contentDescription = "前进10秒",
                    tint = Color.White
                )
            }
        }
    }
}

/**
 * 播放器进度条
 */
@Composable
fun PlayerProgressBar(
    currentPosition: Long,
    totalDuration: Long,
    onSeek: (Long) -> Unit
) {
    val progress = if (totalDuration > 0) {
        (currentPosition.toFloat() / totalDuration.toFloat()).coerceIn(0f, 1f)
    } else {
        0f
    }
    
    Slider(
        value = progress,
        onValueChange = { newProgress ->
            val newPosition = (newProgress * totalDuration).toLong()
            onSeek(newPosition)
        },
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        colors = SliderDefaults.colors(
            thumbColor = Color.White,
            activeTrackColor = Color.Red,
            inactiveTrackColor = Color.White.copy(alpha = 0.3f)
        )
    )
}

/**
 * 格式化时间显示（毫秒转分:秒）
 */
fun formatTime(milliseconds: Long): String {
    val totalSeconds = milliseconds / 1000
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return String.format("%02d:%02d", minutes, seconds)
}

/**
 * 音频播放器界面
 * 用于音频播放，界面更简洁
 */
@Composable
fun AudioPlayerScreen(navController: NavHostController) {
    var isPlaying by remember { mutableStateOf(false) }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        // 专辑封面
        Box(
            modifier = Modifier
                .size(200.dp)
                .background(
                    color = MaterialTheme.colorScheme.primaryContainer,
                    shape = MaterialTheme.shapes.large
                )
        ) {
            Icon(
                imageVector = Icons.Default.MusicNote,
                contentDescription = "专辑封面",
                modifier = Modifier
                    .size(64.dp)
                    .align(Alignment.Center),
                tint = MaterialTheme.colorScheme.primary
            )
        }
        
        // 歌曲信息
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "歌曲名称",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "艺术家",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = "专辑名称",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        
        // 播放控制
        Row(
            horizontalArrangement = Arrangement.spacedBy(32.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { /* 上一首 */ }) {
                Icon(Icons.Default.SkipPrevious, contentDescription = "上一首")
            }
            
            IconButton(
                onClick = { isPlaying = !isPlaying },
                modifier = Modifier.size(64.dp)
            ) {
                Icon(
                    imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                    contentDescription = if (isPlaying) "暂停" else "播放",
                    modifier = Modifier.size(32.dp)
                )
            }
            
            IconButton(onClick = { /* 下一首 */ }) {
                Icon(Icons.Default.SkipNext, contentDescription = "下一首")
            }
        }
        
        // 进度条
        PlayerProgressBar(
            currentPosition = 30000L,
            totalDuration = 180000L,
            onSeek = {}
        )
        
        // 时间显示
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = formatTime(30000L),
                style = MaterialTheme.typography.bodySmall
            )
            Text(
                text = formatTime(180000L),
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}