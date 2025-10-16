package com.trplayer.embyplayer.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Photo
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.trplayer.embyplayer.data.model.FileFormat
import com.trplayer.embyplayer.data.model.MediaItem
import com.trplayer.embyplayer.data.model.MediaType
import com.trplayer.embyplayer.presentation.components.CoilImage

/**
 * 媒体项卡片组件
 * 显示媒体项的图片、标题、描述等信息
 */
@Composable
fun MediaItemCard(
    mediaItem: MediaItem,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {
    Card(
        modifier = modifier
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 媒体封面
            MediaCover(
                mediaItem = mediaItem,
                modifier = Modifier.size(80.dp)
            )
            
            // 媒体信息
            MediaInfo(
                mediaItem = mediaItem,
                modifier = Modifier.weight(1f)
            )
            
            // 媒体类型指示器
            MediaTypeIndicator(
                mediaType = mediaItem.type,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

/**
 * 媒体封面组件
 */
@Composable
fun MediaCover(
    mediaItem: MediaItem,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
    ) {
        if (mediaItem.thumbnailUrl.isNotEmpty()) {
            CoilImage(
                url = mediaItem.thumbnailUrl,
                contentDescription = "媒体封面",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        } else {
            // 默认封面
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = MaterialTheme.colorScheme.primaryContainer,
                shape = RoundedCornerShape(8.dp)
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = getMediaTypeIcon(mediaItem.type),
                        contentDescription = "媒体类型",
                        tint = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.size(32.dp)
                    )
                }
            }
        }
        
        // 播放进度指示器（如果有播放进度）
        mediaItem.playbackProgress?.let { progress ->
            if (progress > 0) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(4.dp)
                        .align(Alignment.BottomStart)
                        .background(Color.Transparent)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .fillMaxWidth(progress / 100f)
                            .background(MaterialTheme.colorScheme.primary)
                    )
                }
            }
        }
    }
}

/**
 * 媒体信息组件
 */
@Composable
fun MediaInfo(
    mediaItem: MediaItem,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        // 标题
        Text(
            text = mediaItem.title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )
        
        // 副标题（如果有）
        if (mediaItem.subtitle.isNotEmpty()) {
            Text(
                text = mediaItem.subtitle,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
        
        // 描述（如果有）
        if (mediaItem.description.isNotEmpty()) {
            Text(
                text = mediaItem.description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
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
            if (mediaItem.userData?.rating != null && mediaItem.userData.rating!! > 0) {
                Text(
                    text = "⭐ ${mediaItem.userData.rating}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            // 时长
            if (mediaItem.duration > 0) {
                Text(
                    text = formatDuration(mediaItem.duration),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        
        // 文件格式信息
        if (mediaItem.isStrmFile || mediaItem.fileFormat != FileFormat.UNKNOWN) {
            Spacer(modifier = Modifier.height(4.dp))
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // strm文件标识
                if (mediaItem.isStrmFile) {
                    Surface(
                        modifier = Modifier
                            .height(20.dp)
                            .padding(horizontal = 6.dp),
                        shape = RoundedCornerShape(10.dp),
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                    ) {
                        Text(
                            text = "STRM",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(horizontal = 4.dp)
                        )
                    }
                }
                
                // 文件格式
                if (mediaItem.fileFormat != FileFormat.UNKNOWN) {
                    Text(
                        text = mediaItem.fileFormat.name,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                    )
                }
            }
        }
    }
}

/**
 * 媒体类型指示器
 */
@Composable
fun MediaTypeIndicator(
    mediaType: MediaType,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = getMediaTypeIcon(mediaType),
            contentDescription = "媒体类型",
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(20.dp)
        )
    }
}

/**
 * 根据媒体类型获取图标
 */
private fun getMediaTypeIcon(mediaType: MediaType) = when (mediaType) {
    MediaType.MOVIE -> Icons.Default.Movie
    MediaType.TV_SHOW -> Icons.Default.Movie
    MediaType.EPISODE -> Icons.Default.Movie
    MediaType.MUSIC -> Icons.Default.MusicNote
    MediaType.ALBUM -> Icons.Default.MusicNote
    MediaType.SONG -> Icons.Default.MusicNote
    MediaType.PHOTO -> Icons.Default.Photo
    MediaType.UNKNOWN -> Icons.Default.Movie
}

/**
 * 格式化时长
 */
private fun formatDuration(duration: Int): String {
    val hours = duration / 3600
    val minutes = (duration % 3600) / 60
    
    return if (hours > 0) {
        "${hours}h ${minutes}m"
    } else {
        "${minutes}m"
    }
}