package com.snstudio.hyper.data.local.repository

import androidx.annotation.WorkerThread
import com.snstudio.hyper.data.local.dao.PlaylistMediaCrossRefDao
import com.snstudio.hyper.data.model.Media
import com.snstudio.hyper.data.model.PlaylistMediaCrossRef
import javax.inject.Inject

class PlaylistMediaCrossRepository @Inject constructor(
    private val playlistMediaCrossRefDao: PlaylistMediaCrossRefDao
) {
    @WorkerThread
    suspend fun getMediaForPlaylistOrdered(playlistId: Long): List<Media> {
        return playlistMediaCrossRefDao.getMediaForPlaylistOrdered(playlistId)
    }

    @WorkerThread
    suspend fun insertMediaListToPlaylist(playlistId: Long, mediaIds: List<String>) {
        val maxOrder = playlistMediaCrossRefDao.getMaxOrderForPlaylist(playlistId) ?: 0
        val crossRefs = mediaIds.mapIndexed { index, mediaId ->
            PlaylistMediaCrossRef(
                playlistId = playlistId,
                id = mediaId,
                order = maxOrder + index + 1
            )
        }
        playlistMediaCrossRefDao.insertAll(crossRefs)
    }

    @WorkerThread
    suspend fun updateOrders(
        playlistId: Long,
        fromId: String,
        fromOrder: Int,
        toId: String,
        toOrder: Int
    ) {
        playlistMediaCrossRefDao.updateOrders(playlistId, fromId, fromOrder, toId, toOrder)
    }

    @WorkerThread
    suspend fun deleteMediaFromPlaylist(playlistId: Long, mediaId: String) {
        playlistMediaCrossRefDao.deleteMediaFromPlaylist(playlistId, mediaId)
    }

}