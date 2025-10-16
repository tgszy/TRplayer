package com.trplayer.embyplayer.presentation.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import coil.compose.AsyncImage
import coil.compose.AsyncImagePainter
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import coil.size.Size
import androidx.compose.ui.platform.LocalContext
import com.trplayer.embyplayer.presentation.image.ImageLoader
import com.trplayer.embyplayer.presentation.image.rememberImageLoader

/**
 * 使用Coil加载图片的Compose组件
 * 支持占位符、错误处理和缓存优化
 */
@Composable
fun CoilImage(
    url: String?,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.Crop,
    placeholder: @Composable (() -> Unit)? = null,
    error: @Composable (() -> Unit)? = null
) {
    val imageLoader = rememberImageLoader()
    
    // 使用Coil的AsyncImage组件
    AsyncImage(
        model = ImageRequest.Builder(LocalContext.current)
            .data(url)
            .size(Size.ORIGINAL)
            .crossfade(true)
            .build(),
        contentDescription = contentDescription,
        modifier = modifier,
        contentScale = contentScale
    )
}

/**
 * 预加载图片到缓存
 */
@Composable
fun PreloadImage(url: String) {
    val imageLoader = rememberImageLoader()
    val context = LocalContext.current
    
    LaunchedEffect(url) {
        if (url.isNotBlank()) {
            imageLoader.preloadImage(context, url)
        }
    }
}

/**
 * 加载图片到Bitmap的Compose函数
 */
@Composable
fun rememberImageBitmap(url: String?): ImageBitmap? {
    var bitmap by remember { mutableStateOf<ImageBitmap?>(null) }
    val imageLoader = rememberImageLoader()
    val context = LocalContext.current
    
    LaunchedEffect(url) {
        if (url != null) {
            val loadedBitmap = imageLoader.loadBitmap(context, url)
            bitmap = loadedBitmap?.asImageBitmap()
        } else {
            bitmap = null
        }
    }
    
    return bitmap
}