package com.snstudio.hyper.shared

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.snstudio.hyper.data.model.Media

object ProgressLiveData {
    private val mediaDownloadStateMLiveData = MutableLiveData<DownloadState>()
    val mediaDownloadStateLiveData: LiveData<DownloadState> = mediaDownloadStateMLiveData

    fun updateDownloadState(state: DownloadState) {
        mediaDownloadStateMLiveData.postValue(state)
    }

    sealed class DownloadState {
        data class Started(val id: String, val message: String) : DownloadState()

        data class InProgress(val media: Media, val progress: Int) : DownloadState()

        data class Completed(val id: String) : DownloadState()

        data object Failed : DownloadState()
    }
}
