package com.snstudio.hyper.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.snstudio.hyper.data.model.Media
import kotlinx.coroutines.flow.Flow


@Dao
interface MediaDao {
    @Query("SELECT * FROM media")
    fun getLocalMedia(): Flow<List<Media>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(media: Media)

    @Update
    suspend fun update(media: Media)

    @Delete
    suspend fun delete(media: Media)

}