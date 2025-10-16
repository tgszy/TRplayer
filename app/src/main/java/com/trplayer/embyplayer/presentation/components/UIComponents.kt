package com.trplayer.embyplayer.presentation.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.foundation.layout.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.background
import androidx.compose.ui.text.font.FontWeight
import com.trplayer.embyplayer.presentation.navigation.Screen

/**
 * 底部导航栏组件
 * 提供应用的主要导航功能
 */
@Composable
fun BottomNavigationBar(
    selectedTab: Screen,
    onTabSelected: (Screen) -> Unit,
    modifier: Modifier = Modifier
) {
    NavigationBar(modifier = modifier) {
        // 首页导航项
        NavigationBarItem(
            selected = selectedTab == Screen.Home,
            onClick = { onTabSelected(Screen.Home) },
            icon = {
                Icon(
                    imageVector = Icons.Default.Home,
                    contentDescription = "首页"
                )
            },
            label = { Text("首页") }
        )

        // 媒体库导航项
        NavigationBarItem(
            selected = selectedTab == Screen.Libraries,
            onClick = { onTabSelected(Screen.Libraries) },
            icon = {
                Icon(
                    imageVector = Icons.Default.Collections,
                    contentDescription = "媒体库"
                )
            },
            label = { Text("媒体库") }
        )

        // 最新内容导航项
        NavigationBarItem(
            selected = selectedTab == Screen.Latest,
            onClick = { onTabSelected(Screen.Latest) },
            icon = {
                Icon(
                    imageVector = Icons.Default.NewReleases,
                    contentDescription = "最新"
                )
            },
            label = { Text("最新") }
        )

        // 下载导航项
        NavigationBarItem(
            selected = selectedTab == Screen.Downloads,
            onClick = { onTabSelected(Screen.Downloads) },
            icon = {
                Icon(
                    imageVector = Icons.Default.Download,
                    contentDescription = "下载"
                )
            },
            label = { Text("下载") }
        )
    }
}

/**
 * 媒体项卡片组件
 * 用于显示单个媒体项目的预览
 */
@Composable
fun MediaItemCard(
    title: String,
    subtitle: String? = null,
    imageUrl: String? = null,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        onClick = onClick,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // 媒体图片区域
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(16f / 9f)
                    .background(
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f),
                        shape = MaterialTheme.shapes.medium
                    )
            ) {
                // 这里可以显示媒体图片
                // 暂时显示占位图标
                Icon(
                    imageVector = Icons.Default.Movie,
                    contentDescription = title,
                    modifier = Modifier
                        .size(48.dp)
                        .align(Alignment.Center),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // 标题和副标题
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Medium,
                maxLines = 1,
                modifier = Modifier.padding(top = 12.dp)
            )

            subtitle?.let {
                Text(
                    text = it,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }
    }
}

/**
 * 加载指示器组件
 * 显示加载状态
 */
@Composable
fun LoadingIndicator(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}

/**
 * 错误提示组件
 * 显示错误信息和重试按钮
 */
@Composable
fun ErrorMessage(
    message: String,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.Error,
            contentDescription = "错误",
            modifier = Modifier.size(64.dp),
            tint = MaterialTheme.colorScheme.error
        )
        
        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(top = 16.dp)
        )
        
        Button(
            onClick = onRetry,
            modifier = Modifier.padding(top = 16.dp)
        ) {
            Text("重试")
        }
    }
}

/**
 * 空状态组件
 * 显示空数据状态
 */
@Composable
fun EmptyState(
    message: String,
    icon: ImageVector = Icons.Default.Inbox,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = "空状态",
            modifier = Modifier.size(64.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
        
        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(top = 16.dp)
        )
    }
}