package com.snstudio.hyper.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.snstudio.hyper.data.model.Media
import com.snstudio.hyper.data.model.PlaylistMediaCrossRef

@Dao
interface PlaylistMediaCrossRefDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(crossRef: PlaylistMediaCrossRef)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(crossRefs: List<PlaylistMediaCrossRef>)

    @Query("DELETE FROM PlaylistMediaCrossRef WHERE playlistId = :playlistId AND id = :mediaId")
    suspend fun deleteMediaFromPlaylist(playlistId: Long, mediaId: String)


    @Query("SELECT MAX(`order`) FROM PlaylistMediaCrossRef WHERE playlistId = :playlistId")
    suspend fun getMaxOrderForPlaylist(playlistId: Long): Int?

    @Transaction
    @Query(
        """
    SELECT Media.* FROM Media
    INNER JOIN PlaylistMediaCrossRef ON Media.id = PlaylistMediaCrossRef.id
    WHERE PlaylistMediaCrossRef.playlistId = :playlistId
    ORDER BY PlaylistMediaCrossRef.`order` ASC
    """
    )
    suspend fun getMediaForPlaylistOrdered(playlistId: Long): List<Media>


    @Query("UPDATE PlaylistMediaCrossRef SET `order` = :newOrder WHERE playlistId = :playlistId AND id = :id")
    suspend fun updateOrder(playlistId: Long, id: String, newOrder: Int)

}
