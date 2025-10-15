package com.trplayer.embyplayer.data.remote.interceptor

import android.content.Context
import com.trplayer.embyplayer.data.local.datastore.EmbyDataStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class EmbyAuthInterceptor @Inject constructor(
    private val context: Context
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        val dataStore = EmbyDataStore(context)
        
        // 获取当前服务器配置
        val serverConfig = runBlocking {
            dataStore.getCurrentServer().first()
        }
        
        // 获取访问令牌
        val accessToken = runBlocking {
            dataStore.getAccessToken().first()
        }
        
        // 构建新的URL
        val newUrl = originalRequest.url.newBuilder()
            .apply {
                serverConfig?.let { config ->
                    scheme(config.scheme)
                    host(config.host)
                    port(config.port)
                    addPathSegment("emby")
                    
                    // 添加认证参数
                    if (!accessToken.isNullOrEmpty()) {
                        addQueryParameter("api_key", accessToken)
                    }
                    
                    // 添加客户端信息
                    addQueryParameter("X-Emby-Client", "EmbyPlayer")
                    addQueryParameter("X-Emby-Device-Name", "Android")
                    addQueryParameter("X-Emby-Device-Id", getDeviceId())
                    addQueryParameter("X-Emby-Client-Version", "1.0.0")
                }
            }
            .build()
        
        val newRequest = originalRequest.newBuilder()
            .url(newUrl)
            .build()
        
        return chain.proceed(newRequest)
    }
    
    private fun getDeviceId(): String {
        // 获取或生成设备ID
        return android.provider.Settings.Secure.getString(
            context.contentResolver,
            android.provider.Settings.Secure.ANDROID_ID
        ) ?: "unknown_device"
    }
}