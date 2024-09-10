package com.snstudio.hyper.module

import android.content.Context
import androidx.room.Room
import com.snstudio.hyper.data.local.AppDatabase
import com.snstudio.hyper.data.local.dao.MediaDao
import com.snstudio.hyper.data.local.dao.PlaylistDao
import com.snstudio.hyper.data.local.dao.PlaylistMediaCrossRefDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object DatabaseModule {
    @Provides
    fun provideLocalMediaDao(appDatabase: AppDatabase): MediaDao {
        return appDatabase.localMediaDao()
    }

    @Provides
    fun providePlaylistDap(appDatabase: AppDatabase): PlaylistDao {
        return appDatabase.playlistDao()
    }

    @Provides
    fun providePlaylistMediaCrossRefDao(appDatabase: AppDatabase): PlaylistMediaCrossRefDao {
        return appDatabase.playlistMediaCrossRefDao()
    }

    @Provides
    @Singleton
    fun provideAppDatabase(
        @ApplicationContext appContext: Context,
    ): AppDatabase {
        return Room.databaseBuilder(
            appContext,
            AppDatabase::class.java,
            "Hyper",
        ).fallbackToDestructiveMigration().build()
    }
}
