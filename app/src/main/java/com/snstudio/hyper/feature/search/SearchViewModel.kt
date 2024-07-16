package com.snstudio.hyper.feature.search

import androidx.databinding.ObservableBoolean
import com.snstudio.hyper.core.base.BaseViewModel
import com.snstudio.hyper.data.model.Media
import com.snstudio.hyper.util.INVOKE
import com.snstudio.hyper.util.RECEIVED
import dagger.hilt.android.lifecycle.HiltViewModel
import io.flutter.plugin.common.MethodChannel
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val methodChannel: MethodChannel
) : BaseViewModel(methodChannel) {

    val searchProgressObservable = ObservableBoolean(false)
    var currentMedia: Media? = null
        private set

    init {
        receivedData(
            RECEIVED.SEARCH_RECEIVED.received,
            RECEIVED.AUDIO_URL_RECEIVED.received,
            RECEIVED.NEXT_RECEIVED.received
        )
    }

    fun search(query: String) {
        searchProgressObservable.set(true)
        methodChannel.invokeMethod(INVOKE.SEARCH.invoke, query)
    }

    fun next() {
        methodChannel.invokeMethod(INVOKE.NEXT.invoke, null)
    }

    fun getAudioUrl(media: Media) {
        currentMedia = media
        methodChannel.invokeMethod(INVOKE.AUDIO_URL.invoke, media.id)
    }

}