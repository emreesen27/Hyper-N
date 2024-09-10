package com.snstudio.hyper.data.local.repository

import androidx.annotation.WorkerThread
import com.snstudio.hyper.data.local.dao.PlaylistDao
import com.snstudio.hyper.data.model.Playlist
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class PlaylistRepository
    @Inject
    constructor(
        private val playlistDao: PlaylistDao,
    ) {
        val allPlaylists: Flow<List<Playlist>> = playlistDao.getAllPlaylists()

        @WorkerThread
        suspend fun insert(playlist: Playlist): Long {
            return playlistDao.insert(playlist)
        }

        @WorkerThread
        suspend fun delete(playlist: Playlist) {
            playlistDao.delete(playlist)
        }
    }
