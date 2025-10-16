package com.trplayer.embyplayer.presentation.components.tv

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text
import com.trplayer.embyplayer.presentation.screens.tv.TvTab

/**
 * 电视导航栏组件
 * 用于电视界面的底部导航
 */
@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun TvNavigationRail(
    selectedTab: TvTab,
    onTabSelected: (TvTab) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 32.dp, vertical = 16.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        TvNavigationItem(
            text = "首页",
            selected = selectedTab == TvTab.HOME,
            onClick = { onTabSelected(TvTab.HOME) }
        )
        
        TvNavigationItem(
            text = "媒体库",
            selected = selectedTab == TvTab.LIBRARY,
            onClick = { onTabSelected(TvTab.LIBRARY) }
        )
        
        TvNavigationItem(
            text = "设置",
            selected = selectedTab == TvTab.SETTINGS,
            onClick = { onTabSelected(TvTab.SETTINGS) }
        )
    }
}

/**
 * 导航项组件
 */
@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
private fun TvNavigationItem(
    text: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    androidx.tv.material3.Surface(
        onClick = onClick,
        modifier = androidx.compose.ui.Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
        shape = androidx.compose.foundation.shape.RoundedCornerShape(8.dp),
        color = if (selected) {
            MaterialTheme.colorScheme.primary
        } else {
            MaterialTheme.colorScheme.surfaceVariant
        }
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 24.dp, vertical = 12.dp),
            style = MaterialTheme.typography.titleMedium,
            color = if (selected) {
                MaterialTheme.colorScheme.onPrimary
            } else {
                MaterialTheme.colorScheme.onSurfaceVariant
            }
        )
    }
}