package com.snstudio.hyper.data.local.repository

import androidx.annotation.WorkerThread
import com.snstudio.hyper.data.local.dao.PlaylistMediaCrossRefDao
import com.snstudio.hyper.data.model.PlaylistMediaCrossRef
import com.snstudio.hyper.data.model.PlaylistWithMedia
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject


class PlaylistMediaCrossRepository @Inject constructor(
    private val playlistMediaCrossRefDao: PlaylistMediaCrossRefDao
) {
    @WorkerThread
    suspend fun insertMediaToPlaylist(playlistId: Long, mediaId: String) {
        val crossRef = PlaylistMediaCrossRef(playlistId, mediaId)
        playlistMediaCrossRefDao.insert(crossRef)
    }

    @WorkerThread
    suspend fun insertMediaListToPlaylist(playlistId: Long, mediaIds: List<String>) {
        val crossRefs = mediaIds.map { mediaId ->
            PlaylistMediaCrossRef(playlistId = playlistId, id = mediaId)
        }
        playlistMediaCrossRefDao.insertAll(crossRefs)
    }

    fun getPlaylistWithMedia(playlistId: Long): Flow<PlaylistWithMedia> {
        return playlistMediaCrossRefDao.getPlaylistWithMedia(playlistId)
    }
}