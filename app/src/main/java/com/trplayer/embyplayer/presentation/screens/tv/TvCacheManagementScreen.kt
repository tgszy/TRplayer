package com.trplayer.embyplayer.presentation.screens.tv

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.tv.material3.Button
import androidx.tv.material3.Card
import androidx.tv.material3.CardDefaults

import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.Icon
import androidx.tv.material3.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Storage
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.OutlinedButton
// import androidx.tv.material3.Scaffold // 移除Scaffold导入，使用Box布局
import androidx.tv.material3.Surface
import androidx.tv.material3.Text
import com.trplayer.embyplayer.presentation.screens.tv.TvTopAppBar

import com.trplayer.embyplayer.presentation.viewmodels.CacheManagementViewModel

/**
 * 电视专用的缓存管理界面
 * 针对大屏幕和遥控器操作优化
 */
@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun TvCacheManagementScreen(
    onBackClick: () -> Unit,
    viewModel: CacheManagementViewModel = hiltViewModel()
) {
    val cacheState by viewModel.cacheState.collectAsState()
    var showConfirmationDialog by remember { mutableStateOf(false) }
    
    LaunchedEffect(Unit) {
        viewModel.loadCacheInfo()
    }
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // 顶部标题栏
            TvTopAppBar(
                title = "缓存管理",
                onBackClick = onBackClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            )
            
            // 主要内容区域
            Surface(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f)
            ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(all = 32.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                // 缓存概览卡片
                TvCacheOverviewCard(
                    cacheSize = cacheState.cacheSize,
                    isLoading = cacheState.isLoading,
                    modifier = Modifier.fillMaxWidth()
                )
                
                Spacer(modifier = Modifier.height(32.dp))
                
                // 缓存类型详情
                TvCacheTypeDetails(
                    mediaCacheSize = cacheState.mediaCacheSize,
                    imageCacheSize = cacheState.imageCacheSize,
                    tempFilesSize = cacheState.tempFilesSize,
                    modifier = Modifier.fillMaxWidth()
                )
                
                Spacer(modifier = Modifier.height(32.dp))
                
                // 操作按钮
                TvCacheActions(
                    onClearAll = { showConfirmationDialog = true },
                    onClearMedia = { viewModel.clearMediaCache() },
                    onClearImages = { viewModel.clearImageCache() },
                    onClearTemp = { viewModel.clearTempFiles() },
                    modifier = Modifier.fillMaxWidth()
                )
                
                Spacer(modifier = Modifier.height(32.dp))
                
                // 缓存设置
                TvCacheSettings(
                    autoCleanEnabled = cacheState.autoCleanEnabled,
                    onAutoCleanToggle = { enabled: Boolean ->
                        viewModel.setAutoCleanEnabled(enabled)
                    },
                    modifier = Modifier.fillMaxWidth()
                )
                }
            }
        }
        
        // 确认对话框
        if (showConfirmationDialog) {
            TvCacheClearConfirmationDialog(
                onConfirm = {
                    viewModel.clearAllCache()
                    showConfirmationDialog = false
                },
                onDismiss = { showConfirmationDialog = false }
            )
        }
    }
}

/**
 * 电视专用的缓存概览卡片
 */
@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun TvCacheOverviewCard(
    cacheSize: Long,
    isLoading: Boolean,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = {},
        modifier = modifier,
        scale = CardDefaults.scale(focusedScale = 1.02f)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Default.Storage,
                contentDescription = "缓存",
                modifier = Modifier.size(64.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                text = "缓存概览",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            if (isLoading) {
                Box(
                    modifier = Modifier.size(48.dp),
                    contentAlignment = Alignment.Center
                ) {
                    // 电视UI使用简单的点状加载指示器
                    Text(
                        text = "●",
                        style = MaterialTheme.typography.displaySmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            } else {
                Text(
                    text = formatFileSize(cacheSize),
                    style = MaterialTheme.typography.displaySmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                text = "总缓存大小",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

/**
 * 电视专用的缓存类型详情
 */
@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun TvCacheTypeDetails(
    mediaCacheSize: Long,
    imageCacheSize: Long,
    tempFilesSize: Long,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = { /* 设置卡片不需要点击事件 */ },
        modifier = modifier,
        scale = CardDefaults.scale(focusedScale = 1.02f)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
        ) {
            Text(
                text = "缓存详情",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // 媒体缓存
            TvCacheTypeItem(
                label = "媒体缓存",
                size = mediaCacheSize,
                modifier = Modifier.fillMaxWidth()
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // 图片缓存
            TvCacheTypeItem(
                label = "图片缓存",
                size = imageCacheSize,
                modifier = Modifier.fillMaxWidth()
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // 临时文件
            TvCacheTypeItem(
                label = "临时文件",
                size = tempFilesSize,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

/**
 * 电视专用的缓存类型项
 */
@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun TvCacheTypeItem(
    label: String,
    size: Long,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.titleMedium
        )
        
        Text(
            text = formatFileSize(size),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Medium
        )
    }
}

/**
 * 电视专用的缓存操作按钮
 */
@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun TvCacheActions(
    onClearAll: () -> Unit,
    onClearMedia: () -> Unit,
    onClearImages: () -> Unit,
    onClearTemp: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
    ) {
        Text(
            text = "缓存操作",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // 清除所有缓存
        Button(
            onClick = onClearAll,
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(
                imageVector = Icons.Default.Delete,
                contentDescription = null,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = "清除所有缓存",
                style = MaterialTheme.typography.titleMedium
            )
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 清除媒体缓存
            OutlinedButton(
                onClick = onClearMedia,
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = "清除媒体",
                    style = MaterialTheme.typography.bodyLarge
                )
            }
            
            // 清除图片缓存
            OutlinedButton(
                onClick = onClearImages,
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = "清除图片",
                    style = MaterialTheme.typography.bodyLarge
                )
            }
            
            // 清除临时文件
            OutlinedButton(
                onClick = onClearTemp,
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = "清除临时文件",
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }
    }
}

/**
 * 电视专用的缓存设置
 */
@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun TvCacheSettings(
    autoCleanEnabled: Boolean,
    onAutoCleanToggle: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = { /* 设置卡片不需要点击事件 */ },
        modifier = modifier,
        scale = CardDefaults.scale(focusedScale = 1.02f)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
        ) {
            Text(
                text = "缓存设置",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // 自动清理设置
            Text(
                text = "自动清理: ${if (autoCleanEnabled) "开启" else "关闭"}",
                style = MaterialTheme.typography.titleMedium
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                text = "自动清理将在缓存超过512MB或文件过期时自动清理",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // TODO: 实现电视专用的开关组件
            Button(
                onClick = { onAutoCleanToggle(!autoCleanEnabled) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = if (autoCleanEnabled) "关闭自动清理" else "开启自动清理",
                    style = MaterialTheme.typography.titleMedium
                )
            }
        }
    }
}

/**
 * 电视专用的缓存清理确认对话框
 */
@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun TvCacheClearConfirmationDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    // TODO: 实现电视专用的对话框组件
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(48.dp),
        contentAlignment = Alignment.Center
    ) {
        Card(
            onClick = { /* 对话框卡片不需要点击事件 */ },
            modifier = Modifier.fillMaxWidth(0.6f)
        ) {
            Column(
                modifier = Modifier.padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "确认清理",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )
                
                Spacer(modifier = Modifier.height(24.dp))
                
                Text(
                    text = "确定要清除所有缓存吗？此操作不可撤销。",
                    style = MaterialTheme.typography.titleMedium,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
                
                Spacer(modifier = Modifier.height(32.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = "取消",
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                    
                    Button(
                        onClick = onConfirm,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = "确认",
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                }
            }
        }
    }
}

/**
 * 文件大小格式化函数
 */
private fun formatFileSize(size: Long): String {
    return when {
        size < 1024 -> "${size} B"
        size < 1024 * 1024 -> "${size / 1024} KB"
        size < 1024 * 1024 * 1024 -> "${size / (1024 * 1024)} MB"
        else -> "${size / (1024 * 1024 * 1024)} GB"
    }
}