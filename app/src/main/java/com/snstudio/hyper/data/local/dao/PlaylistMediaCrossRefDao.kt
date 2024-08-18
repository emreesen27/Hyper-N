package com.snstudio.hyper.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.snstudio.hyper.data.model.PlaylistMediaCrossRef
import com.snstudio.hyper.data.model.PlaylistWithMedia
import kotlinx.coroutines.flow.Flow

@Dao
interface PlaylistMediaCrossRefDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(crossRef: PlaylistMediaCrossRef)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(crossRefs: List<PlaylistMediaCrossRef>)

    @Transaction
    @Query("SELECT * FROM playlist WHERE playlistId = :playlistId")
    fun getPlaylistWithMedia(playlistId: Long): Flow<PlaylistWithMedia>
}
