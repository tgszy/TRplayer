package com.trplayer.embyplayer.presentation.navigation

/**
 * 应用导航配置
 * 定义所有屏幕的路由和导航结构
 */
sealed class Screen(val route: String) {
    /** 主界面 */
    object Main : Screen("main")
    
    /** 首页 */
    object Home : Screen("home")
    
    /** 媒体库 */
    object Libraries : Screen("libraries")
    
    /** 最新内容 */
    object Latest : Screen("latest")
    
    /** 下载管理 */
    object Downloads : Screen("downloads")
    
    /** 服务器设置 */
    object ServerSettings : Screen("server_settings")
    
    /** 播放器界面 */
    object Player : Screen("player")
    
    /** 媒体详情 */
    object MediaDetail : Screen("media_detail")
    
    /** 搜索界面 */
    object Search : Screen("search")
    
    /** 缓存管理界面 */
    object CacheManagement : Screen("cache_management")
}

/**
 * 导航参数常量
 */
object NavParams {
    const val SERVER_ID = "server_id"
    const val LIBRARY_ID = "library_id"
    const val ITEM_ID = "item_id"
    const val MEDIA_TYPE = "media_type"
    const val SEARCH_QUERY = "search_query"
}

/**
 * 导航路由构建器
 */
object NavRoutes {
    /** 主界面路由 */
    val MAIN: String = Screen.Main.route
    
    /** 首页路由 */
    val HOME: String = Screen.Home.route
    
    /** 媒体库路由 */
    val LIBRARIES: String = Screen.Libraries.route
    
    /** 最新内容路由 */
    val LATEST: String = Screen.Latest.route
    
    /** 下载管理路由 */
    val DOWNLOADS: String = Screen.Downloads.route
    
    /** 服务器设置路由 */
    val SERVER_SETTINGS: String = Screen.ServerSettings.route
    
    /** 播放器路由 */
    val PLAYER: String = Screen.Player.route
    
    /** 媒体详情路由 */
    fun mediaDetail(itemId: String): String {
        return "${Screen.MediaDetail.route}/$itemId"
    }
    
    /** 搜索路由 */
    fun search(query: String = ""): String {
        return "${Screen.Search.route}/$query"
    }
    
    /** 缓存管理路由 */
    val CACHE_MANAGEMENT: String = Screen.CacheManagement.route
}