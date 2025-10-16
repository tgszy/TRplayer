package com.trplayer.embyplayer.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp

/**
 * 媒体项加载占位符
 * 在分页加载时显示加载状态
 */
@Composable
fun MediaItemPlaceholder(modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 封面占位符
            Surface(
                modifier = Modifier.size(80.dp),
                shape = RoundedCornerShape(8.dp),
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.1f)
            ) {}
            
            // 信息占位符
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // 标题占位符
                Surface(
                    modifier = Modifier
                        .fillMaxWidth(0.7f)
                        .height(20.dp),
                    shape = RoundedCornerShape(4.dp),
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.1f)
                ) {}
                
                // 副标题占位符
                Surface(
                    modifier = Modifier
                        .fillMaxWidth(0.5f)
                        .height(16.dp),
                    shape = RoundedCornerShape(4.dp),
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.1f)
                ) {}
                
                Spacer(modifier = Modifier.height(4.dp))
                
                // 元数据占位符
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Surface(
                        modifier = Modifier
                            .width(40.dp)
                            .height(14.dp),
                        shape = RoundedCornerShape(4.dp),
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.1f)
                    ) {}
                    
                    Surface(
                        modifier = Modifier
                            .width(50.dp)
                            .height(14.dp),
                        shape = RoundedCornerShape(4.dp),
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.1f)
                    ) {}
                    
                    Surface(
                        modifier = Modifier
                            .width(35.dp)
                            .height(14.dp),
                        shape = RoundedCornerShape(4.dp),
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.1f)
                    ) {}
                }
            }
            
            // 类型指示器占位符
            Surface(
                modifier = Modifier.size(24.dp),
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.1f)
            ) {}
        }
    }
}

/**
 * 网格布局的媒体项占位符
 * 适用于网格布局的加载状态
 */
@Composable
fun MediaGridItemPlaceholder(modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier.padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 封面占位符
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(0.67f),
                shape = RoundedCornerShape(8.dp),
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.1f)
            ) {}
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // 标题占位符
            Surface(
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .height(16.dp),
                shape = RoundedCornerShape(4.dp),
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.1f)
            ) {}
            
            Spacer(modifier = Modifier.height(4.dp))
            
            // 副标题占位符
            Surface(
                modifier = Modifier
                    .fillMaxWidth(0.6f)
                    .height(12.dp),
                shape = RoundedCornerShape(4.dp),
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.1f)
            ) {}
        }
    }
}

/**
 * 电视专用的媒体项占位符
 * 针对大屏幕和遥控器操作优化
 */
@Composable
fun TvMediaItemPlaceholder(modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // 封面占位符（更大的尺寸）
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.1f)
            ) {}
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // 标题占位符（更大的字体）
            Surface(
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .height(24.dp),
                shape = RoundedCornerShape(4.dp),
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.1f)
            ) {}
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // 描述占位符
            Column(
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(16.dp),
                    shape = RoundedCornerShape(4.dp),
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.1f)
                ) {}
                
                Surface(
                    modifier = Modifier
                        .fillMaxWidth(0.9f)
                        .height(16.dp),
                    shape = RoundedCornerShape(4.dp),
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.1f)
                ) {}
            }
        }
    }
}