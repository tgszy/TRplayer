package com.trplayer.embyplayer.data.local.cache

import android.content.Context
import android.content.SharedPreferences
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequest
import androidx.work.WorkManager
import com.trplayer.embyplayer.domain.repository.CacheRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.util.concurrent.TimeUnit
import javax.inject.Inject

/**
 * 缓存管理器
 * 负责媒体缓存、图片缓存和临时文件的清理
 */
class CacheManager @Inject constructor(
    @ApplicationContext private val context: Context
) : CacheRepository {
    
    companion object {
        const val CACHE_CLEANUP_WORK_NAME = "cache_cleanup_work"
        const val CACHE_MAX_SIZE = 1024 * 1024 * 1024L // 1GB
        const val CACHE_EXPIRY_TIME = 7 * 24 * 60 * 60 * 1000L // 7天
        private const val PREFS_NAME = "cache_prefs"
        private const val KEY_AUTO_CLEAN_ENABLED = "auto_clean_enabled"
    }
    
    private val cacheDir = context.cacheDir
    private val externalCacheDir = context.externalCacheDir
    private val workManager = WorkManager.getInstance(context)
    private val sharedPreferences: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    
    /**
     * 清理所有缓存
     */
    override suspend fun clearAllCache(): Boolean {
        return try {
            clearMediaCache()
            clearImageCache()
            clearTempFiles()
            true
        } catch (e: Exception) {
            false
        }
    }
    
    /**
     * 清理媒体缓存
     */
    override suspend fun clearMediaCache(): Boolean {
        return clearDirectory(getMediaCacheDir())
    }
    
    /**
     * 清理图片缓存
     */
    override suspend fun clearImageCache(): Boolean {
        return clearDirectory(getImageCacheDir())
    }
    
    /**
     * 清理临时文件
     */
    override suspend fun clearTempFiles(): Boolean {
        return clearDirectory(getTempDir())
    }
    
    /**
     * 获取总缓存大小
     */
    override suspend fun getTotalCacheSize(): Long {
        return getCacheSize()
    }

    /**
     * 获取媒体缓存大小
     */
    override suspend fun getMediaCacheSize(): Long {
        return getDirectorySize(getMediaCacheDir())
    }

    /**
     * 获取图片缓存大小
     */
    override suspend fun getImageCacheSize(): Long {
        return getDirectorySize(getImageCacheDir())
    }

    /**
     * 获取临时文件大小
     */
    override suspend fun getTempFilesSize(): Long {
        return getDirectorySize(getTempDir())
    }

    /**
     * 获取缓存大小
     */
    override suspend fun getCacheSize(): Long {
        return getDirectorySize(getMediaCacheDir()) +
                getDirectorySize(getImageCacheDir()) +
                getDirectorySize(getTempDir())
    }
    
    /**
     * 检查是否需要清理缓存
     */
    override suspend fun shouldCleanCache(): Boolean {
        val cacheSize = getCacheSize()
        return cacheSize > CACHE_MAX_SIZE || hasExpiredFiles()
    }

    /**
     * 检查是否启用自动清理
     */
    override suspend fun isAutoCleanEnabled(): Boolean {
        return sharedPreferences.getBoolean(KEY_AUTO_CLEAN_ENABLED, true) // 默认启用
    }

    /**
     * 设置自动清理开关
     */
    override suspend fun setAutoCleanEnabled(enabled: Boolean) {
        sharedPreferences.edit().putBoolean(KEY_AUTO_CLEAN_ENABLED, enabled).apply()
        if (enabled) {
            scheduleCacheCleanup()
        } else {
            cancelCacheCleanup()
        }
    }
    
    /**
     * 安排定期缓存清理
     */
    override fun scheduleCacheCleanup() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.NOT_REQUIRED)
            .setRequiresCharging(false)
            .setRequiresBatteryNotLow(true)
            .build()
        
        val cacheCleanupRequest = PeriodicWorkRequest.Builder(
            CacheCleanupWorker::class.java,
            1, TimeUnit.DAYS
        ).setConstraints(constraints)
            .build()
        
        workManager.enqueueUniquePeriodicWork(
            CACHE_CLEANUP_WORK_NAME,
            ExistingPeriodicWorkPolicy.KEEP,
            cacheCleanupRequest
        )
    }
    
    /**
     * 取消缓存清理任务
     */
    override fun cancelCacheCleanup() {
        workManager.cancelUniqueWork(CACHE_CLEANUP_WORK_NAME)
    }
    
    private fun getMediaCacheDir(): File {
        return File(externalCacheDir, "media")
    }
    
    private fun getImageCacheDir(): File {
        return File(cacheDir, "images")
    }
    
    private fun getTempDir(): File {
        return File(cacheDir, "temp")
    }
    
    private suspend fun clearDirectory(directory: File): Boolean {
        return try {
            if (directory.exists() && directory.isDirectory) {
                directory.listFiles()?.forEach { file ->
                    if (file.isDirectory) {
                        clearDirectory(file)
                    } else {
                        file.delete()
                    }
                }
                true
            } else {
                false
            }
        } catch (e: Exception) {
            false
        }
    }
    
    private suspend fun getDirectorySize(directory: File): Long {
        return if (directory.exists() && directory.isDirectory) {
            directory.listFiles()?.sumOf { file ->
                if (file.isDirectory) {
                    getDirectorySize(file)
                } else {
                    file.length()
                }
            } ?: 0L
        } else {
            0L
        }
    }
    
    private suspend fun hasExpiredFiles(): Boolean {
        val currentTime = System.currentTimeMillis()
        return checkDirectoryForExpiredFiles(getMediaCacheDir(), currentTime) ||
                checkDirectoryForExpiredFiles(getImageCacheDir(), currentTime) ||
                checkDirectoryForExpiredFiles(getTempDir(), currentTime)
    }
    
    private suspend fun checkDirectoryForExpiredFiles(directory: File, currentTime: Long): Boolean {
        return if (directory.exists() && directory.isDirectory) {
            directory.listFiles()?.any { file ->
                if (file.isDirectory) {
                    checkDirectoryForExpiredFiles(file, currentTime)
                } else {
                    (currentTime - file.lastModified()) > CACHE_EXPIRY_TIME
                }
            } ?: false
        } else {
            false
        }
    }
}

/**
 * 缓存清理工作器
 */
class CacheCleanupWorker(context: Context, params: androidx.work.WorkerParameters) 
    : androidx.work.CoroutineWorker(context, params) {
    
    override suspend fun doWork(): Result {
        return try {
            val cacheManager = CacheManager(applicationContext)
            if (cacheManager.shouldCleanCache()) {
                cacheManager.clearAllCache()
                Result.success()
            } else {
                Result.success()
            }
        } catch (e: Exception) {
            Result.retry()
        }
    }
}