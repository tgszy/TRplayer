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
import androidx.tv.material3.Text
import com.trplayer.embyplayer.presentation.screens.tv.TvTab

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
        Text(
            text = "首页",
            modifier = Modifier.padding(16.dp)
        )
        
        Text(
            text = "媒体库", 
            modifier = Modifier.padding(16.dp)
        )
        
        Text(
            text = "设置",
            modifier = Modifier.padding(16.dp)
        )
    }
}
