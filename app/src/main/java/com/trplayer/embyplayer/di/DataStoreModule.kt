package com.trplayer.embyplayer.di

import android.content.Context
import com.trplayer.embyplayer.data.local.datastore.EmbyDataStore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataStoreModule {

    @Provides
    @Singleton
    fun provideEmbyDataStore(@ApplicationContext context: Context): EmbyDataStore {
        return EmbyDataStore(context)
    }
}