package com.trplayer.embyplayer.presentation.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.MaterialTheme as TvMaterialTheme
import androidx.tv.material3.darkColorScheme as tvDarkColorScheme
import androidx.tv.material3.lightColorScheme as tvLightColorScheme
import com.trplayer.embyplayer.BuildConfig
import com.trplayer.embyplayer.presentation.theme.TvTypography
import com.trplayer.embyplayer.presentation.theme.TvShapes

/**
 * 安卓电视主题配置
 * 针对大屏幕和遥控器操作优化
 */

// 电视亮色主题配色
@OptIn(ExperimentalTvMaterial3Api::class)
private val TvLightColorScheme = tvLightColorScheme(
    primary = Color(0xFF0066CC),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFD6E3FF),
    onPrimaryContainer = Color(0xFF001A43),
    secondary = Color(0xFF545F71),
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFD8E3F8),
    onSecondaryContainer = Color(0xFF111C2B),
    tertiary = Color(0xFF6D5776),
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFFF6DAFF),
    onTertiaryContainer = Color(0xFF261532),
    background = Color(0xFFFDFBFF),
    onBackground = Color(0xFF1A1C1E),
    surface = Color(0xFFFDFBFF),
    onSurface = Color(0xFF1A1C1E),
    surfaceVariant = Color(0xFFE0E2EC),
    onSurfaceVariant = Color(0xFF43474E)
)

// 电视深色主题配色
@OptIn(ExperimentalTvMaterial3Api::class)
private val TvDarkColorScheme = tvDarkColorScheme(
    primary = Color(0xFFABC7FF),
    onPrimary = Color(0xFF002E6B),
    primaryContainer = Color(0xFF004396),
    onPrimaryContainer = Color(0xFFD6E3FF),
    secondary = Color(0xFFBCC7DB),
    onSecondary = Color(0xFF273141),
    secondaryContainer = Color(0xFF3D4758),
    onSecondaryContainer = Color(0xFFD8E3F8),
    tertiary = Color(0xFFD9BEE2),
    onTertiary = Color(0xFF3C2A45),
    tertiaryContainer = Color(0xFF53405C),
    onTertiaryContainer = Color(0xFFF6DAFF),
    background = Color(0xFF1A1C1E),
    onBackground = Color(0xFFE2E2E6),
    surface = Color(0xFF1A1C1E),
    onSurface = Color(0xFFE2E2E6),
    surfaceVariant = Color(0xFF43474E),
    onSurfaceVariant = Color(0xFFC4C6D0)
)

/**
 * 电视主题
 */
@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun TvTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) {
        TvDarkColorScheme
    } else {
        TvLightColorScheme
    }
    
    TvMaterialTheme(
        colorScheme = colorScheme,
        typography = TvTypography,
        shapes = TvShapes,
        content = content
    )
}

/**
 * 电视和手机通用主题
 * 根据设备类型自动适配
 */
@Composable
fun AdaptiveTheme(
    isTv: Boolean = BuildConfig.IS_TV,
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    if (isTv) {
        TvTheme(darkTheme = darkTheme, content = content)
    } else {
        MaterialTheme(
            colorScheme = if (darkTheme) darkColorScheme() else lightColorScheme(),
            typography = Typography,
            content = content
        )
    }
}