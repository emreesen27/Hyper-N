package com.snstudio.hyper.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.snstudio.hyper.data.Media
import kotlinx.coroutines.flow.Flow


@Dao
interface MediaDao {
    @Query("SELECT * FROM media")
    fun getLocalMedia(): Flow<List<Media>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(media: Media)

    @Delete()
    suspend fun delete(media: Media)

}