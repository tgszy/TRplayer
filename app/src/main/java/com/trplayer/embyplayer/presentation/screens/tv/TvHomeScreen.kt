package com.trplayer.embyplayer.presentation.screens.tv

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.Icon
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text
import androidx.tv.material3.CardDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import com.trplayer.embyplayer.data.model.MediaItem
import com.trplayer.embyplayer.presentation.components.tv.TvMediaGrid
import com.trplayer.embyplayer.presentation.components.tv.TvNavigationRail

/**
 * 电视主界面
 * 针对大屏幕和遥控器操作优化
 */
@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun TvHomeScreen(
    onNavigateToSettings: () -> Unit,
    onNavigateToCacheManagement: () -> Unit,
    onNavigateToPlayerSettings: () -> Unit,
    onPlayMedia: (mediaId: String) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableStateOf(TvTab.HOME) }
    
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // 水平布局：导航栏 + 内容区域
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // 顶部标题栏
            TvTopAppBar(
                title = "TRplayer",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            )
            
            // 主要内容区域
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f)
            ) {
                when (selectedTab) {
                    TvTab.HOME -> TvHomeContent(
                        onPlayMedia = onPlayMedia,
                        modifier = Modifier.fillMaxSize()
                    )
                    TvTab.LIBRARY -> TvLibraryContent(
                        onPlayMedia = onPlayMedia,
                        modifier = Modifier.fillMaxSize()
                    )
                    TvTab.SETTINGS -> TvSettingsContent(
                        onNavigateToSettings = onNavigateToSettings,
                        onNavigateToCacheManagement = onNavigateToCacheManagement,
                        onNavigateToPlayerSettings = onNavigateToPlayerSettings,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
            
            // 底部导航栏
            TvNavigationRail(
                selectedTab = selectedTab,
                onTabSelected = { tab -> selectedTab = tab },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 32.dp)
            )
        }
    }
}

/**
 * 电视顶部应用栏
 */
@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun TvTopAppBar(
    title: String = "TRplayer",
    onBackClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 后退按钮（如果有）
        if (onBackClick != null) {
            androidx.tv.material3.IconButton(
                onClick = onBackClick,
                modifier = Modifier.size(48.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "返回"
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
        }
        
        // 标题
        Text(
            text = title,
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.onBackground
        )
    }
}

/**
 * 电视首页内容
 */
@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun TvHomeContent(
    onPlayMedia: (mediaId: String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(horizontal = 32.dp)
    ) {
        // 继续观看
        Text(
            text = "继续观看",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        
        // 媒体网格
        TvMediaGrid(
            mediaItems = listOf<MediaItem>(), // TODO: 从ViewModel获取数据
            onItemClick = { mediaItem -> onPlayMedia(mediaItem.id) },
            modifier = Modifier.fillMaxWidth()
        )
    }
}

/**
 * 电视媒体库内容
 */
@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun TvLibraryContent(
    onPlayMedia: (mediaId: String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(horizontal = 32.dp)
    ) {
        Text(
            text = "媒体库",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        
        // 媒体库网格
        TvMediaGrid(
            mediaItems = listOf<MediaItem>(), // TODO: 从ViewModel获取数据
            onItemClick = { mediaItem -> onPlayMedia(mediaItem.id) },
            modifier = Modifier.fillMaxWidth()
        )
    }
}

/**
 * 电视设置内容
 */
@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun TvSettingsContent(
    onNavigateToSettings: () -> Unit,
    onNavigateToCacheManagement: () -> Unit,
    onNavigateToPlayerSettings: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(horizontal = 32.dp)
    ) {
        Text(
            text = "设置",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        
        // 设置选项列表
        TvSettingsOption(
            title = "缓存管理",
            description = "清理媒体缓存和临时文件",
            onClick = onNavigateToCacheManagement
        )
        
        TvSettingsOption(
            title = "服务器设置",
            description = "管理Emby服务器连接",
            onClick = onNavigateToSettings
        )
        
        TvSettingsOption(
            title = "播放设置",
            description = "配置播放器参数",
            onClick = onNavigateToPlayerSettings
        )
    }
}

/**
 * 电视设置选项组件
 */
@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun TvSettingsOption(
    title: String,
    description: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    androidx.tv.material3.Card(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        scale = androidx.tv.material3.CardDefaults.scale(focusedScale = 1.02f)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            Text(
                text = description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
            )
        }
    }
}

/**
 * 电视标签枚举
 */
enum class TvTab {
    HOME, LIBRARY, SETTINGS
}