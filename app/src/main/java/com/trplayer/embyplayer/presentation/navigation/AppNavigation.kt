package com.trplayer.embyplayer.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.trplayer.embyplayer.presentation.screens.*

/**
 * 应用导航配置
 * 定义所有屏幕的导航关系和路由
 */
@Composable
fun AppNavigation(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = NavRoutes.MAIN
    ) {
        // 主界面导航图
        composable(NavRoutes.MAIN) {
            MainScreen(navController)
        }
        
        // 首页导航图
        composable(NavRoutes.HOME) {
            HomeScreen(navController)
        }
        
        // 媒体库导航图
        composable(NavRoutes.LIBRARIES) {
            LibrariesScreen(navController)
        }
        
        // 最新内容导航图
        composable(NavRoutes.LATEST) {
            LatestScreen(navController)
        }
        
        // 下载管理导航图
        composable(NavRoutes.DOWNLOADS) {
            DownloadsScreen(navController)
        }
        
        // 服务器设置导航图
        composable(NavRoutes.SERVER_SETTINGS) {
            // ServerSettingsScreen(navController)
            // 暂时显示占位界面
            PlaceholderScreen("服务器设置")
        }
        
        // 播放器导航图
        composable(NavRoutes.PLAYER) {
            // PlayerScreen(navController)
            // 暂时显示占位界面
            PlaceholderScreen("播放器")
        }
        
        // 媒体详情导航图（带参数）
        composable(
            route = "${Screen.MediaDetail.route}/{${NavParams.ITEM_ID}}"
        ) { backStackEntry ->
            val itemId = backStackEntry.arguments?.getString(NavParams.ITEM_ID) ?: ""
            // MediaDetailScreen(navController, itemId)
            // 暂时显示占位界面
            PlaceholderScreen("媒体详情: $itemId")
        }
        
        // 搜索导航图（带参数）
        composable(
            route = "${Screen.Search.route}/{${NavParams.SEARCH_QUERY}}"
        ) { backStackEntry ->
            val query = backStackEntry.arguments?.getString(NavParams.SEARCH_QUERY) ?: ""
            // SearchScreen(navController, query)
            // 暂时显示占位界面
            PlaceholderScreen("搜索: $query")
        }
    }
}

/**
 * 占位界面（用于未实现的屏幕）
 */
@Composable
fun PlaceholderScreen(screenName: String) {
    androidx.compose.material3.Text(
        text = "$screenName 界面开发中...",
        modifier = androidx.compose.ui.Modifier
            .fillMaxSize()
            .wrapContentSize()
    )
}