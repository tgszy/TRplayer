package com.trplayer.embyplayer.presentation.image

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import coil.ImageLoader as CoilImageLoader
import coil.request.ImageRequest
import coil.request.SuccessResult
import coil.disk.DiskCache
import coil.memory.MemoryCache
import coil.util.DebugLogger
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import java.io.File
import java.io.FileOutputStream
import java.net.URL
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 图片加载管理器
 * 使用Coil进行高效的图片加载和缓存管理
 */
@Singleton
class ImageLoader @Inject constructor(
    private val context: Context
) {
    
    private val memoryCache = MemoryCache.Builder()
        .maxSizePercent(0.25) // 使用25%的可用内存作为图片缓存
        .build()
    
    private val diskCache = DiskCache.Builder()
        .directory(File(context.cacheDir, "image_cache"))
        .maxSizeBytes(100L * 1024 * 1024) // 100MB磁盘缓存
        .build()
    
    // 创建Coil图片加载器
    val coilImageLoader: CoilImageLoader by lazy {
        CoilImageLoader.Builder(context)
            .memoryCache(memoryCache)
            .diskCache(diskCache)
            .respectCacheHeaders(false) // 忽略服务器缓存头，使用自定义缓存策略
            .logger(DebugLogger())
            .build()
    }
    
    /**
     * 加载图片到Bitmap
     */
    suspend fun loadBitmap(url: String): Bitmap? {
        return try {
            val request = ImageRequest.Builder(context)
                .data(url)
                .allowHardware(false)
                .build()
            
            val result = coilImageLoader.execute(request)
            if (result is SuccessResult) {
                result.image.toBitmap()
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
    suspend fun preloadImage(url: String) {
        try {
            val request = ImageRequest.Builder(context)
                .data(url)
                .size(100, 100) // 预加载小尺寸图片
                .build()
            
            coilImageLoader.enqueue(request)
        } catch (e: Exception) {
            // 预加载失败不影响主流程
            e.printStackTrace()
        }
    }
    
    /**
     * 清除内存缓存
     */
    fun clearMemoryCache() {
        memoryCache.clear()
    }
    
    /**
     * 清除磁盘缓存
     */
    fun clearDiskCache() {
        diskCache.clear()
    }
    
    /**
     * 获取缓存大小
     */
    suspend fun getCacheSize(): Long {
        return withContext(Dispatchers.IO) {
            diskCache.size
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
    val context = LocalContext.current
    return remember {
        ImageLoader(context)
    }
}