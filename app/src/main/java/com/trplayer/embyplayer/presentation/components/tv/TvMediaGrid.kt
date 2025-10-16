package com.trplayer.embyplayer.presentation.components.tv

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.paging.compose.LazyPagingItems
import com.trplayer.embyplayer.data.model.MediaItem

/**
 * 电视专用的媒体网格组件
 * 基于TvPagedMediaList实现，提供简化的网格布局
 */
@Composable
fun TvMediaGrid(
    mediaItems: LazyPagingItems<MediaItem>,
    modifier: Modifier = Modifier,
    columns: Int = 5,
    onItemClick: (MediaItem) -> Unit = {},
    onItemFocused: (MediaItem?) -> Unit = {}
) {
    TvPagedMediaList(
        mediaItems = mediaItems,
        modifier = modifier,
        gridState = rememberLazyGridState(),
        columns = columns,
        onItemClick = onItemClick,
        onItemFocused = onItemFocused
    )
}

/**
 * 电视专用的媒体网格组件（列表版本）
 * 用于非分页的媒体列表
 */
@Composable
fun TvMediaGrid(
    mediaItems: List<MediaItem>,
    modifier: Modifier = Modifier,
    columns: Int = 5,
    onItemClick: (MediaItem) -> Unit = {},
    onItemFocused: (MediaItem?) -> Unit = {}
) {
    LazyVerticalGrid(
        state = rememberLazyGridState(),
        columns = GridCells.Fixed(columns),
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(
            items = mediaItems,
            key = { it.id }
        ) { mediaItem ->
            TvMediaItemCard(
                mediaItem = mediaItem,
                onClick = { onItemClick(mediaItem) },
                onFocusChanged = { focused ->
                    if (focused) {
                        onItemFocused(mediaItem)
                    }
                }
            )
        }
    }
}