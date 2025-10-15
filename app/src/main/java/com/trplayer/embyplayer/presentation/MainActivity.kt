package com.trplayer.embyplayer.presentation

import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.rememberNavController
import com.trplayer.embyplayer.BuildConfig
import com.trplayer.embyplayer.presentation.navigation.AppNavigation
import com.trplayer.embyplayer.presentation.screens.tv.TvHomeScreen
import com.trplayer.embyplayer.presentation.theme.AdaptiveTheme
import dagger.hilt.android.AndroidEntryPoint

/**
 * 主Activity - 应用的入口点
 * 使用Hilt进行依赖注入，设置Compose UI
 * 支持多平台：安卓电视、安卓手机
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        setContent {
            EmbyPlayerApp()
        }
    }
}

/**
 * Emby播放器应用主组件
 * 包含主题设置和导航控制，支持多平台适配
 */
@Composable
fun EmbyPlayerApp() {
    val context = LocalContext.current
    val isTvDevice = isTvDevice(context)
    
    AdaptiveTheme(isTv = isTvDevice) {
        // 使用Surface作为根布局，确保正确的主题应用
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            if (isTvDevice) {
                // 电视界面 - 使用简单的状态管理进行界面切换
                var currentScreen by remember { mutableStateOf<TvScreen>(TvScreen.HOME) }
                
                when (currentScreen) {
                    TvScreen.HOME -> TvHomeScreen(
                        onNavigateToSettings = { currentScreen = TvScreen.SETTINGS },
                        onNavigateToCacheManagement = { currentScreen = TvScreen.CACHE_MANAGEMENT },
                        onNavigateToPlayerSettings = { currentScreen = TvScreen.PLAYER_SETTINGS },
                        onPlayMedia = { mediaId -> /* TODO: 播放媒体 */ }
                    )
                    TvScreen.SETTINGS -> {
                        // TODO: 实现电视设置界面
                        TvPlaceholderScreen(
                            title = "设置",
                            onBackClick = { currentScreen = TvScreen.HOME }
                        )
                    }
                    TvScreen.CACHE_MANAGEMENT -> TvCacheManagementScreen(
                        onBackClick = { currentScreen = TvScreen.HOME }
                    )
                    TvScreen.PLAYER_SETTINGS -> {
                        // TODO: 实现播放设置界面
                        TvPlaceholderScreen(
                            title = "播放设置",
                            onBackClick = { currentScreen = TvScreen.HOME }
                        )
                    }
                }
            } else {
                // 手机界面
                val navController = rememberNavController()
                AppNavigation(navController = navController)
            }
        }
    }
}

/**
 * 电视界面枚举
 */
enum class TvScreen {
    HOME, SETTINGS, CACHE_MANAGEMENT, PLAYER_SETTINGS
}

/**
 * 电视占位界面（用于未实现的屏幕）
 */
@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun TvPlaceholderScreen(
    title: String,
    onBackClick: () -> Unit
) {
    Scaffold(
        topBar = {
            TvTopAppBar(
                title = title,
                onBackClick = onBackClick
            )
        }
    ) { innerPadding ->
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "$title 界面开发中...",
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }
        }
    }
}

/**
 * 检测是否为电视设备
 */
private fun isTvDevice(context: android.content.Context): Boolean {
    // 优先使用构建配置的设备类型
    if (BuildConfig.IS_TV) return true
    if (BuildConfig.IS_PHONE) return false
    
    // 运行时检测设备类型
    val packageManager = context.packageManager
    return packageManager.hasSystemFeature(PackageManager.FEATURE_LEANBACK) ||
           packageManager.hasSystemFeature(PackageManager.FEATURE_TELEVISION)
}