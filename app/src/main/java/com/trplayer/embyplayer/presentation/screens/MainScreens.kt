package com.trplayer.embyplayer.presentation.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.trplayer.embyplayer.presentation.components.BottomNavigationBar
import com.trplayer.embyplayer.presentation.navigation.Screen

/**
 * 主界面 - 应用的主要入口点
 * 包含底部导航和各个功能页面的容器
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(navController: NavHostController) {
    var selectedTab by remember { mutableStateOf<Screen>(Screen.Home) }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Emby播放器",
                        fontWeight = FontWeight.Bold
                    )
                },
                actions = {
                    IconButton(onClick = { /* 搜索功能 */ }) {
                        Icon(Icons.Default.Search, contentDescription = "搜索")
                    }
                    IconButton(onClick = { /* 设置功能 */ }) {
                        Icon(Icons.Default.Settings, contentDescription = "设置")
                    }
                }
            )
        },
        bottomBar = {
            BottomNavigationBar(
                selectedTab = selectedTab,
                onTabSelected = { tab ->
                    selectedTab = tab
                    navController.navigate(tab.route) {
                        // 清除返回栈，避免重复导航
                        popUpTo(navController.graph.startDestinationId) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // 根据选中的标签显示不同的内容
            when (selectedTab) {
                Screen.Home -> HomeScreen(navController)
                Screen.Libraries -> LibrariesScreen(navController)
                Screen.Latest -> LatestScreen(navController)
                Screen.Downloads -> DownloadsScreen(navController)
                else -> HomeScreen(navController) // 默认显示首页
            }
        }
    }
}

/**
 * 首页界面
 * 显示欢迎信息、快速访问和推荐内容
 */
@Composable
fun HomeScreen(navController: NavHostController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 欢迎卡片
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            )
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "欢迎使用Emby播放器",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "连接您的Emby服务器，随时随地享受媒体内容",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
        }

        // 快速访问网格
        Text(
            text = "快速访问",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 继续观看
            QuickAccessCard(
                title = "继续观看",
                icon = Icons.Default.PlayArrow,
                onClick = { navController.navigate(Screen.Libraries.route) },
                modifier = Modifier.weight(1f)
            )
            
            // 最新添加
            QuickAccessCard(
                title = "最新添加",
                icon = Icons.Default.NewReleases,
                onClick = { navController.navigate(Screen.Latest.route) },
                modifier = Modifier.weight(1f)
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 电影
            QuickAccessCard(
                title = "电影",
                icon = Icons.Default.Movie,
                onClick = { /* 导航到电影页面 */ },
                modifier = Modifier.weight(1f)
            )
            
            // 电视剧
            QuickAccessCard(
                title = "电视剧",
                icon = Icons.Default.Tv,
                onClick = { /* 导航到电视剧页面 */ },
                modifier = Modifier.weight(1f)
            )
        }

        // 推荐内容区域
        Text(
            text = "推荐内容",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(top = 16.dp)
        )
        
        // 这里可以显示推荐的内容列表
        // 暂时显示占位文本
        Text(
            text = "推荐内容将在这里显示",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

/**
 * 快速访问卡片组件
 */
@Composable
fun QuickAccessCard(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(1f),
        onClick = onClick,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                modifier = Modifier.size(32.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(top = 8.dp)
            )
        }
    }
}

/**
 * 媒体库界面
 * 显示用户的所有媒体库和内容
 */
@Composable
fun LibrariesScreen(navController: NavHostController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "媒体库",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )
        
        // 这里将显示媒体库列表
        // 暂时显示占位文本
        Text(
            text = "媒体库列表将在这里显示",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(top = 16.dp)
        )
    }
}

/**
 * 最新内容界面
 * 显示最新添加的媒体内容
 */
@Composable
fun LatestScreen(navController: NavHostController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "最新添加",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )
        
        // 这里将显示最新添加的内容
        // 暂时显示占位文本
        Text(
            text = "最新添加的内容将在这里显示",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(top = 16.dp)
        )
    }
}

/**
 * 下载界面
 * 显示已下载的媒体内容和下载管理
 */
@Composable
fun DownloadsScreen(navController: NavHostController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "下载",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )
        
        // 这里将显示下载的内容
        // 暂时显示占位文本
        Text(
            text = "下载的内容将在这里显示",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(top = 16.dp)
        )
    }
}