package com.trplayer.embyplayer.presentation.screens

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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.trplayer.embyplayer.presentation.components.CommonTopAppBar
import com.trplayer.embyplayer.presentation.viewmodels.CacheManagementViewModel

/**
 * 缓存管理界面
 * 提供缓存清理、大小查看和设置功能
 */
@Composable
fun CacheManagementScreen(
    onBackClick: () -> Unit,
    viewModel: CacheManagementViewModel = hiltViewModel()
) {
    val cacheState by viewModel.cacheState.collectAsState()
    var showConfirmationDialog by remember { mutableStateOf(false) }
    
    LaunchedEffect(Unit) {
        viewModel.loadCacheInfo()
    }
    
    Scaffold(
        topBar = {
            CommonTopAppBar(
                title = "缓存管理",
                onBackClick = onBackClick
            )
        }
    ) { innerPadding ->
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                // 缓存概览卡片
                CacheOverviewCard(
                    cacheSize = cacheState.cacheSize,
                    isLoading = cacheState.isLoading,
                    modifier = Modifier.fillMaxWidth()
                )
                
                Spacer(modifier = Modifier.height(24.dp))
                
                // 缓存类型详情
                CacheTypeDetails(
                    mediaCacheSize = cacheState.mediaCacheSize,
                    imageCacheSize = cacheState.imageCacheSize,
                    tempFilesSize = cacheState.tempFilesSize,
                    modifier = Modifier.fillMaxWidth()
                )
                
                Spacer(modifier = Modifier.height(32.dp))
                
                // 操作按钮
                CacheActions(
                    onClearAll = { showConfirmationDialog = true },
                    onClearMedia = { viewModel.clearMediaCache() },
                    onClearImages = { viewModel.clearImageCache() },
                    onClearTemp = { viewModel.clearTempFiles() },
                    modifier = Modifier.fillMaxWidth()
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // 缓存设置
                CacheSettings(
                    autoCleanEnabled = cacheState.autoCleanEnabled,
                    onAutoCleanToggle = { enabled ->
                        viewModel.setAutoCleanEnabled(enabled)
                    },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
        
        // 确认对话框
        if (showConfirmationDialog) {
            CacheClearConfirmationDialog(
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
 * 缓存概览卡片
 */
@Composable
fun CacheOverviewCard(
    cacheSize: Long,
    isLoading: Boolean,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Default.Storage,
                contentDescription = "缓存",
                modifier = Modifier.size(48.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = "缓存概览",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            if (isLoading) {
                CircularProgressIndicator()
            } else {
                Text(
                    text = formatFileSize(cacheSize),
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = "总缓存大小",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

/**
 * 缓存类型详情
 */
@Composable
fun CacheTypeDetails(
    mediaCacheSize: Long,
    imageCacheSize: Long,
    tempFilesSize: Long,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = "缓存详情",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // 媒体缓存
            CacheTypeItem(
                label = "媒体缓存",
                size = mediaCacheSize,
                modifier = Modifier.fillMaxWidth()
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // 图片缓存
            CacheTypeItem(
                label = "图片缓存",
                size = imageCacheSize,
                modifier = Modifier.fillMaxWidth()
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // 临时文件
            CacheTypeItem(
                label = "临时文件",
                size = tempFilesSize,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

/**
 * 缓存类型项
 */
@Composable
fun CacheTypeItem(
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
            style = MaterialTheme.typography.bodyMedium
        )
        
        Text(
            text = formatFileSize(size),
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium
        )
    }
}

/**
 * 缓存操作按钮
 */
@Composable
fun CacheActions(
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
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // 清除所有缓存
        Button(
            onClick = onClearAll,
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(
                imageVector = Icons.Default.Delete,
                contentDescription = null,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = "清除所有缓存")
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // 清除媒体缓存
            OutlinedButton(
                onClick = onClearMedia,
                modifier = Modifier.weight(1f)
            ) {
                Text(text = "清除媒体")
            }
            
            // 清除图片缓存
            OutlinedButton(
                onClick = onClearImages,
                modifier = Modifier.weight(1f)
            ) {
                Text(text = "清除图片")
            }
            
            // 清除临时文件
            OutlinedButton(
                onClick = onClearTemp,
                modifier = Modifier.weight(1f)
            ) {
                Text(text = "清除临时")
            }
        }
    }
}

/**
 * 缓存设置
 */
@Composable
fun CacheSettings(
    autoCleanEnabled: Boolean,
    onAutoCleanToggle: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = "缓存设置",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // 自动清理开关
            // TODO: 实现开关组件
            Text(
                text = "自动清理: ${if (autoCleanEnabled) "开启" else "关闭"}",
                style = MaterialTheme.typography.bodyMedium
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = "自动清理将在缓存超过512MB或文件过期时自动清理",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

/**
 * 文件大小格式化
 */
private fun formatFileSize(size: Long): String {
    return when {
        size < 1024 -> "${size} B"
        size < 1024 * 1024 -> "${String.format("%.1f", size / 1024.0)} KB"
        size < 1024 * 1024 * 1024 -> "${String.format("%.1f", size / (1024.0 * 1024.0))} MB"
        else -> "${String.format("%.1f", size / (1024.0 * 1024.0 * 1024.0))} GB"
    }
}