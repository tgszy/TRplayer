package com.trplayer.embyplayer.presentation.components.tv

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.tv.material3.Card
import androidx.tv.material3.CardDefaults
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.Icon
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Surface
import androidx.tv.material3.Text
import coil.compose.AsyncImage
import com.trplayer.embyplayer.data.model.MediaItem
import com.trplayer.embyplayer.data.model.MediaType

/**
 * 电视专用的媒体项卡片组件
 * 针对大屏幕和遥控器操作优化，支持焦点状态和缩放效果
 */
@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun TvMediaItemCard(
    mediaItem: MediaItem,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
    onFocusChanged: (Boolean) -> Unit = {}
) {
    var isFocused by remember { mutableStateOf(false) }
    
    Card(
        modifier = modifier
            .aspectRatio(0.67f)
            .onFocusChanged { focusState ->
                isFocused = focusState.isFocused
                onFocusChanged(focusState.isFocused)
            }
            .focusable(),
        shape = CardDefaults.shape(shape = RoundedCornerShape(12.dp)),
        scale = CardDefaults.scale(focusedScale = 1.05f),
        glow = CardDefaults.glow(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        onClick = onClick
    ) {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            // 媒体封面
            TvMediaCover(
                mediaItem = mediaItem,
                isFocused = isFocused,
                modifier = Modifier.fillMaxSize()
            )
            
            // 底部信息栏
            TvMediaInfoBar(
                mediaItem = mediaItem,
                isFocused = isFocused,
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .fillMaxWidth()
            )
            
            // 播放进度指示器
            mediaItem.playbackProgress?.let { progress ->
                if (progress > 0) {
                    TvPlaybackProgress(
                        progress = progress,
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .fillMaxWidth()
                            .height(4.dp)
                    )
                }
            }
            
            // 媒体类型指示器
            TvMediaTypeIndicator(
                mediaType = mediaItem.type,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(12.dp)
                    .size(32.dp)
            )
        }
    }
}

/**
 * 电视专用的媒体封面组件
 */
@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun TvMediaCover(
    mediaItem: MediaItem,
    isFocused: Boolean,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
    ) {
        if (mediaItem.thumbnailUrl.isNotEmpty()) {
            AsyncImage(
                model = mediaItem.thumbnailUrl,
                contentDescription = "媒体封面",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        } else {
            // 默认封面
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = MaterialTheme.colorScheme.primaryContainer,
                shape = RoundedCornerShape(12.dp)
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = getTvMediaTypeIcon(mediaItem.type),
                        contentDescription = "媒体类型",
                        tint = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.size(48.dp)
                    )
                }
            }
        }
        
        // 焦点状态边框
        if (isFocused) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .border(
                        width = 3.dp,
                        color = MaterialTheme.colorScheme.primary,
                        shape = RoundedCornerShape(12.dp)
                    )
            )
        }
        
        // 渐变遮罩
        Box(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .fillMaxWidth()
                .height(80.dp)
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color.Transparent,
                            Color.Black.copy(alpha = 0.7f)
                        )
                    )
                )
        )
    }
}

/**
 * 电视专用的媒体信息栏
 */
@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun TvMediaInfoBar(
    mediaItem: MediaItem,
    isFocused: Boolean,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        // 标题
        Text(
            text = mediaItem.title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            color = Color.White
        )
        
        // 副标题（如果有）
        if (mediaItem.subtitle.isNotEmpty()) {
            Text(
                text = mediaItem.subtitle,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                color = Color.White.copy(alpha = 0.8f)
            )
        }
        
        Spacer(modifier = Modifier.height(4.dp))
        
        // 元数据信息
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // 年份
            if (mediaItem.year > 0) {
                Text(
                    text = mediaItem.year.toString(),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            
            // 评分
            if (mediaItem.rating > 0) {
                Text(
                    text = "⭐ ${mediaItem.rating}",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.White.copy(alpha = 0.8f)
                )
            }
            
            // 时长
            if (mediaItem.duration > 0) {
                Text(
                    text = formatTvDuration(mediaItem.duration),
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.White.copy(alpha = 0.8f)
                )
            }
        }
    }
}

/**
 * 电视专用的播放进度指示器
 */
@Composable
fun TvPlaybackProgress(
    progress: Float,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
    ) {
        // 背景条
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.5f))
        )
        
        // 进度条
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth(progress / 100f)
                .background(MaterialTheme.colorScheme.primary)
        )
    }
}

/**
 * 电视专用的媒体类型指示器
 */
@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun TvMediaTypeIndicator(
    mediaType: MediaType,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.9f)
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = getTvMediaTypeIcon(mediaType),
                contentDescription = "媒体类型",
                tint = Color.White,
                modifier = Modifier.size(18.dp)
            )
        }
    }
}

/**
 * 根据媒体类型获取电视专用图标
 */
private fun getTvMediaTypeIcon(mediaType: MediaType) = when (mediaType) {
    MediaType.MOVIE -> androidx.tv.material3.icons.Icons.Default.Movie
    MediaType.TV_SHOW -> androidx.tv.material3.icons.Icons.Default.Tv
    MediaType.EPISODE -> androidx.tv.material3.icons.Icons.Default.PlayArrow
    MediaType.MUSIC -> androidx.tv.material3.icons.Icons.Default.MusicNote
    MediaType.ALBUM -> androidx.tv.material3.icons.Icons.Default.Album
    MediaType.SONG -> androidx.tv.material3.icons.Icons.Default.MusicNote
    MediaType.PHOTO -> androidx.tv.material3.icons.Icons.Default.Photo
    MediaType.UNKNOWN -> androidx.tv.material3.icons.Icons.Default.Movie
}

/**
 * 格式化电视专用的时长显示
 */
private fun formatTvDuration(duration: Int): String {
    val hours = duration / 3600
    val minutes = (duration % 3600) / 60
    
    return if (hours > 0) {
        "${hours}h ${minutes}m"
    } else {
        "${minutes}m"
    }
}