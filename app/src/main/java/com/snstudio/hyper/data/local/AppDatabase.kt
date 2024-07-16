package com.snstudio.hyper.data.local


import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.snstudio.hyper.data.model.Media
import com.snstudio.hyper.data.model.Playlist
import com.snstudio.hyper.data.model.PlaylistMediaCrossRef
import com.snstudio.hyper.data.local.dao.MediaDao
import com.snstudio.hyper.data.local.dao.PlaylistDao
import com.snstudio.hyper.data.local.dao.PlaylistMediaCrossRefDao

@Database(
    entities = [Media::class, Playlist::class, PlaylistMediaCrossRef::class],
    version = 1,
    exportSchema = false,
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun localMediaDao(): MediaDao
    abstract fun playlistDao(): PlaylistDao
    abstract fun playlistMediaCrossRefDao(): PlaylistMediaCrossRefDao

}