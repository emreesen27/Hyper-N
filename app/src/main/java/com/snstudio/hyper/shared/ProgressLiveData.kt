package com.snstudio.hyper.shared

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

object ProgressLiveData {
    private val _mediaDownloadStateLiveData = MutableLiveData<DownloadState>()
    val mediaDownloadStateLiveData: LiveData<DownloadState> = _mediaDownloadStateLiveData

    fun updateDownloadState(state: DownloadState) {
        _mediaDownloadStateLiveData.postValue(state)
    }

    sealed class DownloadState {
        data class Started(val message: String) : DownloadState()
        data class InProgress(val progress: Int) : DownloadState()
        data object Completed : DownloadState()
        data object Failed : DownloadState()
    }

}