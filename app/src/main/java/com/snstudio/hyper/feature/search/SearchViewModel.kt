package com.snstudio.hyper.feature.search

import androidx.databinding.ObservableBoolean
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.snstudio.hyper.core.base.BaseViewModel
import com.snstudio.hyper.data.local.repository.MediaRepository
import com.snstudio.hyper.data.model.Media
import com.snstudio.hyper.util.INVOKE
import com.snstudio.hyper.util.PrefsTag
import com.snstudio.hyper.util.RECEIVED
import com.snstudio.hyper.util.SharedPreferenceManager
import dagger.hilt.android.lifecycle.HiltViewModel
import io.flutter.plugin.common.MethodChannel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchViewModel
    @Inject
    constructor(
        private val methodChannel: MethodChannel,
        private val localMediaRepository: MediaRepository,
        private val sharedPreferenceManager: SharedPreferenceManager,
    ) : BaseViewModel(methodChannel) {
        private val localMediaListMLiveData = MutableLiveData<List<Media>>()

        val searchProgressObservable = ObservableBoolean(false)
        val searchResultIsEmptyObservable = ObservableBoolean(false)

        var audioActionType: AudioActionType = AudioActionType.PLAY
            private set

        var currentMedia: Media? = null
            private set

        init {
            receivedData(
                RECEIVED.SEARCH_RECEIVED.received,
                RECEIVED.AUDIO_URL_RECEIVED.received,
                RECEIVED.NEXT_RECEIVED.received,
            )
            collectLocalMediaData()
        }

        fun invokeSearch(query: String) {
            searchProgressObservable.set(true)
            methodChannel.invokeMethod(INVOKE.SEARCH.invoke, query)
        }

        fun invokeNext() {
            methodChannel.invokeMethod(INVOKE.NEXT.invoke, null)
        }

        fun invokeAudioUrl(
            media: Media,
            type: AudioActionType,
        ) {
            audioActionType = type
            currentMedia = media
            methodChannel.invokeMethod(INVOKE.AUDIO_URL.invoke, media.id)
        }

        fun insertMediaJob(media: Media) {
            val job = Job()
            val scope = CoroutineScope(Dispatchers.IO + job)
            scope.launch {
                try {
                    localMediaRepository.insert(media)
                } finally {
                    job.cancel()
                }
            }
        }

        private fun collectLocalMediaData() =
            viewModelScope.launch {
                localMediaRepository.localMediaList.collect {
                    localMediaListMLiveData.value = it
                }
            }

        fun setTrueInfoDialogStatus() {
            sharedPreferenceManager.putBoolean(PrefsTag.DOWNLOAD_INFO_DIALOG_SHOW, true)
        }

        fun getInfoDialogStatus(): Boolean = sharedPreferenceManager.getBoolean(PrefsTag.DOWNLOAD_INFO_DIALOG_SHOW)

        fun isMediaSavedLocally(media: Media): Boolean = localMediaListMLiveData.value?.any { media.id == it.id } ?: false

        enum class AudioActionType {
            PLAY,
            DOWNLOAD,
        }
    }
