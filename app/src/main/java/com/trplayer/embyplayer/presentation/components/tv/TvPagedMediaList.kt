package com.trplayer.embyplayer.presentation.components.tv

import androidx.compose.foundation.background
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Surface
import androidx.tv.material3.Text
import com.trplayer.embyplayer.data.model.MediaItem
import com.trplayer.embyplayer.presentation.components.MediaGridItemPlaceholder
import com.trplayer.embyplayer.presentation.components.tv.TvMediaItemCard

/**
 * 电视专用的分页媒体网格列表
 * 针对大屏幕和遥控器操作优化
 */
@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun TvPagedMediaList(
    mediaItems: LazyPagingItems<MediaItem>,
    modifier: Modifier = Modifier,
    gridState: LazyGridState = rememberLazyGridState(),
    columns: Int = 5,
    onItemClick: (MediaItem) -> Unit = {},
    onItemFocused: (MediaItem?) -> Unit = {},
    onPreloadTrigger: (visibleIndices: List<Int>, totalCount: Int) -> Unit = { _, _ -> }
) {
    val visibleItemIndices by remember {
        derivedStateOf {
            val layoutInfo = gridState.layoutInfo
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
    
    Surface(
        modifier = modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.surface
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            LazyVerticalGrid(
                state = gridState,
                columns = GridCells.Fixed(columns),
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(
                    count = mediaItems.itemCount,
                    key = { index -> mediaItems[index]?.id ?: index }
                ) { index ->
                    val mediaItem = mediaItems[index]
                    val focusRequester = remember { FocusRequester() }
                    
                    if (mediaItem != null) {
                        TvMediaItemCard(
                            mediaItem = mediaItem,
                            modifier = Modifier
                                .focusable()
                                .focusRequester(focusRequester),
                            onClick = { onItemClick(mediaItem) },
                            onFocusChanged = { focused ->
                                if (focused) {
                                    onItemFocused(mediaItem)
                                }
                            }
                        )
                    } else {
                        // 加载占位符
                        MediaGridItemPlaceholder(
                            modifier = Modifier
                                .height(280.dp)
                                .focusable()
                        )
                    }
                }
                
                // 加载状态处理
                mediaItems.apply {
                    when {
                        loadState.refresh is LoadState.Loading -> {
                            item(span = { androidx.compose.foundation.lazy.grid.GridItemSpan(columns) }) {
                                TvLoadingIndicator(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(200.dp)
                                        .padding(16.dp)
                                )
                            }
                        }
                        
                        loadState.append is LoadState.Loading -> {
                            item(span = { androidx.compose.foundation.lazy.grid.GridItemSpan(columns) }) {
                                TvLoadingIndicator(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(120.dp)
                                        .padding(16.dp)
                                )
                            }
                        }
                        
                        loadState.refresh is LoadState.Error -> {
                            val error = loadState.refresh as LoadState.Error
                            item(span = { androidx.compose.foundation.lazy.grid.GridItemSpan(columns) }) {
                                TvErrorMessage(
                                    message = "加载失败: ${error.error.message}",
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(200.dp)
                                        .padding(16.dp)
                                )
                            }
                        }
                        
                        loadState.append is LoadState.Error -> {
                            val error = loadState.append as LoadState.Error
                            item(span = { androidx.compose.foundation.lazy.grid.GridItemSpan(columns) }) {
                                TvErrorMessage(
                                    message = "加载更多失败: ${error.error.message}",
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(120.dp)
                                        .padding(16.dp)
                                )
                            }
                        }
                    }
                }
            }
            
            // 空状态处理
            if (mediaItems.itemCount == 0 && mediaItems.loadState.refresh !is LoadState.Loading) {
                TvEmptyState(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                )
            }
        }
    }
}

/**
 * 电视专用的加载指示器
 */
@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun TvLoadingIndicator(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 使用简单的点状加载指示器替代CircularProgressIndicator
            Text(
                text = "●",
                style = MaterialTheme.typography.displaySmall,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = "加载中...",
                style = MaterialTheme.typography.titleMedium
            )
        }
    }
}

/**
 * 电视专用的错误消息
 */
@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun TvErrorMessage(message: String, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "❌",
                style = MaterialTheme.typography.displaySmall
            )
            Text(
                text = message,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.error
            )
        }
    }
}

/**
 * 电视专用的空状态
 */
@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun TvEmptyState(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "📺",
                style = MaterialTheme.typography.displayMedium
            )
            Text(
                text = "暂无内容",
                style = MaterialTheme.typography.titleLarge
            )
            Text(
                text = "当前媒体库为空，请添加媒体内容或检查网络连接",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}