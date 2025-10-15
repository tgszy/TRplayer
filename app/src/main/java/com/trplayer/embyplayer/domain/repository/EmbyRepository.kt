package com.trplayer.embyplayer.domain.repository

import com.trplayer.embyplayer.data.remote.model.*
import com.trplayer.embyplayer.domain.model.EmbyItem
import com.trplayer.embyplayer.domain.model.EmbyLibrary
import com.trplayer.embyplayer.domain.model.EmbyUser
import kotlinx.coroutines.flow.Flow

/**
 * Emby存储库接口
 * 定义了与Emby服务器交互的所有业务逻辑操作
 * 包括用户认证、媒体库浏览、播放控制等功能
 */
interface EmbyRepository {
    
    // ==================== 用户认证相关 ====================
    
    /**
     * 用户登录认证
     * @param userId 用户名
     * @param password 密码（可选）
     * @return 认证结果，包含用户信息和访问令牌
     */
    suspend fun authenticateUser(userId: String, password: String? = null): Result<AuthenticationResult>
    
    /**
     * 获取服务器上的公开用户列表
     * @return 用户列表
     */
    suspend fun getPublicUsers(): Result<List<EmbyUser>>
    
    /**
     * 根据用户ID获取用户详细信息
     * @param userId 用户ID
     * @return 用户详细信息
     */
    suspend fun getUserById(userId: String): Result<EmbyUser>
    
    // ==================== 媒体库和媒体项相关 ====================
    
    /**
     * 获取用户可见的媒体库列表
     * @param userId 用户ID
     * @return 媒体库列表
     */
    suspend fun getUserLibraries(userId: String): Result<List<EmbyLibrary>>
    
    /**
     * 获取媒体库中的媒体项
     * @param userId 用户ID
     * @param parentId 父级目录ID（可选）
     * @param itemTypes 媒体类型过滤（可选）
     * @param startIndex 起始索引，用于分页
     * @param limit 每页数量
     * @return 媒体项列表
     */
    suspend fun getLibraryItems(
        userId: String, 
        parentId: String? = null,
        itemTypes: List<String>? = null,
        startIndex: Int = 0,
        limit: Int = 100
    ): Result<List<EmbyItem>>
    
    /**
     * 获取媒体项详细信息
     * @param userId 用户ID
     * @param itemId 媒体项ID
     * @return 媒体项详细信息
     */
    suspend fun getItemDetails(userId: String, itemId: String): Result<EmbyItem>
    
    /**
     * 获取最新添加的媒体项
     * @param userId 用户ID
     * @param limit 返回数量限制
     * @return 最新媒体项列表
     */
    suspend fun getLatestItems(userId: String, limit: Int = 20): Result<List<EmbyItem>>
    
    /**
     * 获取用户继续观看的媒体项
     * @param userId 用户ID
     * @param limit 返回数量限制
     * @return 继续观看的媒体项列表
     */
    suspend fun getResumeItems(userId: String, limit: Int = 20): Result<List<EmbyItem>>
    
    // ==================== 搜索相关 ====================
    
    /**
     * 搜索媒体项
     * @param userId 用户ID
     * @param searchTerm 搜索关键词
     * @param limit 返回数量限制
     * @return 搜索结果列表
     */
    suspend fun searchItems(userId: String, searchTerm: String, limit: Int = 20): Result<List<EmbyItem>>
    
    // ==================== 播放控制相关 ====================
    
    /**
     * 获取媒体播放信息
     * @param userId 用户ID
     * @param itemId 媒体项ID
     * @return 播放信息，包含媒体源信息
     */
    suspend fun getPlaybackInfo(userId: String, itemId: String): Result<PlaybackInfoResponse>
    
    /**
     * 报告播放开始
     * @param request 播放开始请求信息
     * @return 操作结果
     */
    suspend fun reportPlaybackStart(request: PlaybackStartRequest): Result<Unit>
    
    /**
     * 报告播放进度
     * @param request 播放进度请求信息
     * @return 操作结果
     */
    suspend fun reportPlaybackProgress(request: PlaybackProgressRequest): Result<Unit>
    
    /**
     * 报告播放停止
     * @param request 播放停止请求信息
     * @return 操作结果
     */
    suspend fun reportPlaybackStopped(request: PlaybackStopRequest): Result<Unit>
    
    // ==================== 本地数据存储相关 ====================
    
    /**
     * 获取当前登录用户ID的流
     * @return 用户ID流
     */
    fun getCurrentUser(): Flow<String?>
    
    /**
     * 获取当前连接的服务器信息的流
     * @return 服务器信息流
     */
    fun getCurrentServer(): Flow<com.trplayer.embyplayer.domain.model.EmbyServer?>
    
    /**
     * 设置当前登录用户
     * @param userId 用户ID
     * @param userName 用户名
     */
    suspend fun setCurrentUser(userId: String, userName: String)
    
    /**
     * 设置当前连接的服务器
     * @param server 服务器信息
     */
    suspend fun setCurrentServer(server: com.trplayer.embyplayer.domain.model.EmbyServer)
    
    /**
     * 清除所有认证信息
     */
    suspend fun clearAuthentication()
}