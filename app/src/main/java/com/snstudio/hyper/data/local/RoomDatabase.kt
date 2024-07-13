package com.snstudio.hyper.data.local


import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.snstudio.hyper.data.Media


@Database(
    entities = [Media::class],
    version = 1,
    exportSchema = false,
)
@TypeConverters(Converters::class)
abstract class RoomDatabase : RoomDatabase() {
    abstract fun localMediaDao(): MediaDao

}