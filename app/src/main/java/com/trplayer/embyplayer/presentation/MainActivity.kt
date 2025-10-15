package com.trplayer.embyplayer.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.trplayer.embyplayer.presentation.navigation.AppNavigation
import com.trplayer.embyplayer.presentation.theme.EmbyPlayerTheme
import dagger.hilt.android.AndroidEntryPoint

/**
 * 主Activity - 应用的入口点
 * 使用Hilt进行依赖注入，设置Compose UI
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
 * 包含主题设置和导航控制
 */
@Composable
fun EmbyPlayerApp() {
    EmbyPlayerTheme {
        // 使用Surface作为根布局，确保正确的主题应用
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            // 导航控制器
            val navController = rememberNavController()
            
            // 应用导航
            AppNavigation(navController = navController)
        }
    }
}