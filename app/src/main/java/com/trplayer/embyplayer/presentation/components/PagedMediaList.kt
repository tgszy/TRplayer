package com.trplayer.embyplayer.presentation.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.itemContentType
import androidx.paging.compose.itemKey
import com.trplayer.embyplayer.data.model.MediaItem
import com.trplayer.embyplayer.presentation.components.LoadingIndicator

/**
 * 分页媒体列表组件
 * 支持分页加载、预加载和错误处理
 */
@Composable
fun PagedMediaList(
    mediaItems: LazyPagingItems<MediaItem>,
    modifier: Modifier = Modifier,
    listState: LazyListState = rememberLazyListState(),
    onItemClick: (MediaItem) -> Unit = {},
    onPreloadTrigger: (visibleIndices: List<Int>, totalCount: Int) -> Unit = { _, _ -> }
) {
    val visibleItemIndices by remember {
        derivedStateOf {
            val layoutInfo = listState.layoutInfo
            layoutInfo.visibleItemsInfo.map { it.index }
        }
    }
    
    val totalItemCount by remember {
        derivedStateOf {
            mediaItems.itemCount
        }
    }
    
    // 监听可见项变化，触发预加载
    LaunchedEffect(visibleItemIndices) {
        if (visibleItemIndices.isNotEmpty()) {
            onPreloadTrigger(visibleItemIndices, totalItemCount)
        }
    }
    
    Surface(modifier = modifier.fillMaxSize()) {
        Box(modifier = Modifier.fillMaxSize()) {
            LazyColumn(
                state = listState,
                modifier = Modifier.fillMaxSize()
            ) {
                items(
                    count = mediaItems.itemCount,
                    key = mediaItems.itemKey { it.id },
                    contentType = mediaItems.itemContentType { "media_item" }
                ) { index ->
                    val mediaItem = mediaItems[index]
                    
                    if (mediaItem != null) {
                        MediaItemCard(
                            mediaItem = mediaItem,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp),
                            onClick = { onItemClick(mediaItem) }
                        )
                    } else {
                        // 加载占位符
                        MediaItemPlaceholder(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp)
                        )
                    }
                }
                
                // 加载状态处理
                mediaItems.apply {
                    when {
                        loadState.refresh is LoadState.Loading -> {
                            item {
                                LoadingIndicator(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(16.dp)
                                )
                            }
                        }
                        
                        loadState.append is LoadState.Loading -> {
                            item {
                                LoadingIndicator(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp)
                                )
                            }
                        }
                        
                        loadState.refresh is LoadState.Error -> {
                            val error = loadState.refresh as LoadState.Error
                            item {
                                ErrorMessage(
                                    message = "加载失败: ${error.error.message}",
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(16.dp)
                                )
                            }
                        }
                        
                        loadState.append is LoadState.Error -> {
                            val error = loadState.append as LoadState.Error
                            item {
                                ErrorMessage(
                                    message = "加载更多失败: ${error.error.message}",
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp)
                                )
                            }
                        }
                    }
                }
            }
            
            // 空状态处理
            if (mediaItems.itemCount == 0 && mediaItems.loadState.refresh !is LoadState.Loading) {
                EmptyState(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                )
            }
        }
    }
}

/**
 * 错误消息
 */
@Composable
fun ErrorMessage(message: String, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = message,
            color = androidx.compose.material3.MaterialTheme.colorScheme.error
        )
    }
}

/**
 * 空状态
 */
@Composable
fun EmptyState(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "暂无内容",
            color = androidx.compose.material3.MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}