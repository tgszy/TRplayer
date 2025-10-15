package com.trplayer.embyplayer

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class EmbyPlayerApplication : Application() {
    
    override fun onCreate() {
        super.onCreate()
        // 初始化应用级别的配置
    }
}