package com.trplayer.embyplayer.data.local.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.trplayer.embyplayer.domain.model.EmbyServer
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import javax.inject.Inject

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "emby_preferences")

class EmbyDataStore @Inject constructor(
    private val context: Context
) {
    
    companion object {
        private val CURRENT_SERVER = stringPreferencesKey("current_server")
        private val ACCESS_TOKEN = stringPreferencesKey("access_token")
        private val USER_ID = stringPreferencesKey("user_id")
        private val USER_NAME = stringPreferencesKey("user_name")
        private val SERVERS = stringPreferencesKey("servers")
    }
    
    private val json = Json { 
        ignoreUnknownKeys = true
        isLenient = true
    }
    
    // 当前服务器配置
    val currentServer: Flow<EmbyServer?> = context.dataStore.data
        .map { preferences ->
            preferences[CURRENT_SERVER]?.let { serverJson ->
                json.decodeFromString<EmbyServer>(serverJson)
            }
        }
    
    suspend fun setCurrentServer(server: EmbyServer) {
        context.dataStore.edit { preferences ->
            preferences[CURRENT_SERVER] = json.encodeToString(server)
        }
    }
    
    // 访问令牌
    val accessToken: Flow<String?> = context.dataStore.data
        .map { preferences ->
            preferences[ACCESS_TOKEN]
        }
    
    suspend fun setAccessToken(token: String) {
        context.dataStore.edit { preferences ->
            preferences[ACCESS_TOKEN] = token
        }
    }
    
    // 用户信息
    val userId: Flow<String?> = context.dataStore.data
        .map { preferences ->
            preferences[USER_ID]
        }
    
    suspend fun setUserId(userId: String) {
        context.dataStore.edit { preferences ->
            preferences[USER_ID] = userId
        }
    }
    
    val userName: Flow<String?> = context.dataStore.data
        .map { preferences ->
            preferences[USER_NAME]
        }
    
    suspend fun setUserName(userName: String) {
        context.dataStore.edit { preferences ->
            preferences[USER_NAME] = userName
        }
    }
    
    // 服务器列表
    val servers: Flow<List<EmbyServer>> = context.dataStore.data
        .map { preferences ->
            preferences[SERVERS]?.let { serversJson ->
                json.decodeFromString<List<EmbyServer>>(serversJson)
            } ?: emptyList()
        }
    
    suspend fun setServers(servers: List<EmbyServer>) {
        context.dataStore.edit { preferences ->
            preferences[SERVERS] = json.encodeToString(servers)
        }
    }
    
    // 清除所有数据
    suspend fun clearAll() {
        context.dataStore.edit { preferences ->
            preferences.clear()
        }
    }
    
    // 获取方法（用于协程外部调用）
    suspend fun getCurrentServer(): EmbyServer? {
        return context.dataStore.data.map { preferences ->
            preferences[CURRENT_SERVER]?.let { serverJson ->
                json.decodeFromString<EmbyServer>(serverJson)
            }
        }.first()
    }
    
    suspend fun getAccessToken(): String? {
        return context.dataStore.data.map { preferences ->
            preferences[ACCESS_TOKEN]
        }.first()
    }
    
    suspend fun getUserId(): String? {
        return context.dataStore.data.map { preferences ->
            preferences[USER_ID]
        }.first()
    }
    
    suspend fun getUserName(): String? {
        return context.dataStore.data.map { preferences ->
            preferences[USER_NAME]
        }.first()
    }
    
    suspend fun getServers(): List<EmbyServer> {
        return context.dataStore.data.map { preferences ->
            preferences[SERVERS]?.let { serversJson ->
                json.decodeFromString<List<EmbyServer>>(serversJson)
            } ?: emptyList()
        }.first()
    }
}