package com.trplayer.embyplayer.di

import android.content.Context
import com.trplayer.embyplayer.data.local.datastore.EmbyDataStore
import com.trplayer.embyplayer.data.remote.api.EmbyApiService
import com.trplayer.embyplayer.data.remote.interceptor.EmbyAuthInterceptor
import com.trplayer.embyplayer.presentation.player.ExoPlayerManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideHttpLoggingInterceptor(): HttpLoggingInterceptor {
        return HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
    }

    @Provides
    @Singleton
    fun provideEmbyAuthInterceptor(@ApplicationContext context: Context, dataStore: EmbyDataStore): EmbyAuthInterceptor {
        return EmbyAuthInterceptor(context, dataStore)
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(
        loggingInterceptor: HttpLoggingInterceptor,
        authInterceptor: EmbyAuthInterceptor
    ): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .addInterceptor(authInterceptor)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl("http://localhost:8096/emby/") // 默认URL，将在运行时动态更改
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideEmbyApiService(retrofit: Retrofit): EmbyApiService {
        return retrofit.create(EmbyApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideExoPlayerManager(
        context: Context,
        apiService: EmbyApiService
    ): ExoPlayerManager {
        return ExoPlayerManager(context, apiService)
    }
}