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
    suspend fun addMediaToPlaylist(playlistId: Long, mediaId: String) {
        val crossRef = PlaylistMediaCrossRef(playlistId, mediaId)
        playlistMediaCrossRefDao.insert(crossRef)
    }

    fun getPlaylistWithMedia(playlistId: Long): Flow<PlaylistWithMedia> {
        return playlistMediaCrossRefDao.getPlaylistWithMedia(playlistId)
    }
}