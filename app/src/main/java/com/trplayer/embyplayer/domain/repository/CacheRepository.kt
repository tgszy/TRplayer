package com.trplayer.embyplayer.domain.repository

/**
 * 缓存仓库接口
 * 定义缓存管理相关的操作
 */
interface CacheRepository {
    
    /**
     * 清理所有缓存
     */
    suspend fun clearAllCache(): Boolean
    
    /**
     * 清理媒体缓存
     */
    suspend fun clearMediaCache(): Boolean
    
    /**
     * 清理图片缓存
     */
    suspend fun clearImageCache(): Boolean
    
    /**
     * 清理临时文件
     */
    suspend fun clearTempFiles(): Boolean
    
    /**
     * 获取缓存大小
     */
    suspend fun getCacheSize(): Long
    
    /**
     * 检查是否需要清理缓存
     */
    suspend fun shouldCleanCache(): Boolean
    
    /**
     * 安排定期缓存清理
     */
    fun scheduleCacheCleanup()
    
    /**
     * 取消缓存清理任务
     */
    fun cancelCacheCleanup()
    
    /**
     * 获取总缓存大小
     */
    suspend fun getTotalCacheSize(): Long
    
    /**
     * 获取媒体缓存大小
     */
    suspend fun getMediaCacheSize(): Long
    
    /**
     * 获取图片缓存大小
     */
    suspend fun getImageCacheSize(): Long
    
    /**
     * 获取临时文件大小
     */
    suspend fun getTempFilesSize(): Long
    
    /**
     * 检查是否启用自动清理
     */
    suspend fun isAutoCleanEnabled(): Boolean

    /**
     * 设置自动清理开关
     */
    suspend fun setAutoCleanEnabled(enabled: Boolean)
}