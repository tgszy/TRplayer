package com.trplayer.embyplayer.presentation.image

import android.content.Context
import android.graphics.Bitmap
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import coil.ImageLoader as CoilImageLoader
import coil.request.ImageRequest
import coil.request.SuccessResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * 图片加载管理器
 * 使用Coil进行高效的图片加载和缓存管理
 */
class ImageLoader {
    
    /**
     * 加载图片到Bitmap
     */
    suspend fun loadBitmap(context: Context, url: String): Bitmap? {
        return try {
            val request = ImageRequest.Builder(context)
                .data(url)
                .allowHardware(false)
                .build()
            
            val imageLoader = CoilImageLoader.Builder(context).build()
            val result = imageLoader.execute(request)
            if (result is SuccessResult) {
                // 使用drawable而不是image属性
                val drawable = result.drawable
                if (drawable != null) {
                    // 将drawable转换为bitmap
                    val bitmap = Bitmap.createBitmap(
                        drawable.intrinsicWidth,
                        drawable.intrinsicHeight,
                        Bitmap.Config.ARGB_8888
                    )
                    val canvas = android.graphics.Canvas(bitmap)
                    drawable.setBounds(0, 0, canvas.width, canvas.height)
                    drawable.draw(canvas)
                    bitmap
                } else {
                    null
                }
            } else {
                null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
    
    /**
     * 预加载图片到缓存
     */
    suspend fun preloadImage(context: Context, url: String) {
        try {
            val request = ImageRequest.Builder(context)
                .data(url)
                .size(100, 100) // 预加载小尺寸图片
                .build()
            
            val imageLoader = CoilImageLoader.Builder(context).build()
            imageLoader.enqueue(request)
        } catch (e: Exception) {
            // 预加载失败不影响主流程
            e.printStackTrace()
        }
    }
}

/**
 * 图片加载状态
 */
sealed class ImageLoadState {
    object Loading : ImageLoadState()
    data class Success(val bitmap: Bitmap) : ImageLoadState()
    data class Error(val exception: Exception) : ImageLoadState()
}

/**
 * 记住图片加载器
 */
@Composable
fun rememberImageLoader(): ImageLoader {
    return remember {
        ImageLoader()
    }
}