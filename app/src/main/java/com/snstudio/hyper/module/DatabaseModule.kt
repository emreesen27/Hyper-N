package com.snstudio.hyper.module

import android.content.Context
import androidx.room.Room
import com.snstudio.hyper.data.local.MediaDao
import com.snstudio.hyper.data.local.RoomDatabase
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
    fun provideLocalMediaDao(roomDatabase: RoomDatabase): MediaDao {
        return roomDatabase.localMediaDao()
    }

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext appContext: Context): RoomDatabase {
        return Room.databaseBuilder(
            appContext,
            RoomDatabase::class.java,
            "Hyper"
        ).allowMainThreadQueries().build()
    }
}