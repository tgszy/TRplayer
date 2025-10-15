package com.trplayer.embyplayer.presentation.player

import android.app.Service
import android.content.Intent
import android.os.IBinder

/**
 * 音频播放器服务
 * 用于后台音频播放
 */
class AudioPlayerService : Service() {
    
    override fun onBind(intent: Intent?): IBinder? {
        // 绑定服务，返回null表示不支持绑定
        return null
    }
    
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        // 启动服务
        return START_NOT_STICKY
    }
    
    override fun onCreate() {
        super.onCreate()
        // 初始化音频播放器
    }
    
    override fun onDestroy() {
        super.onDestroy()
        // 清理资源
    }
}