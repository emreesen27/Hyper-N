package com.snstudio.hyper.data.local.repository


import androidx.annotation.WorkerThread
import com.snstudio.hyper.data.model.Media
import com.snstudio.hyper.data.local.dao.MediaDao
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class MediaRepository @Inject constructor(private val mediaDao: MediaDao) {

    val localMediaList: Flow<List<Media>> = mediaDao.getLocalMedia()

    @WorkerThread
    suspend fun insert(media: Media) {
        return mediaDao.insert(media)
    }

    @WorkerThread
    suspend fun delete(media: Media) {
        return mediaDao.delete(media)
    }
}