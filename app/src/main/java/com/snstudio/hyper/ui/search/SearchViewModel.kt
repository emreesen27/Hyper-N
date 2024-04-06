package com.snstudio.hyper.ui.search

import androidx.databinding.ObservableBoolean
import com.snstudio.hyper.util.INVOKE
import com.snstudio.hyper.util.RECEIVED
import com.snstudio.hyper.core.base.BaseViewModel
import com.snstudio.hyper.data.Media
import dagger.hilt.android.lifecycle.HiltViewModel
import io.flutter.plugin.common.MethodChannel
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val methodChannel: MethodChannel
) : BaseViewModel(methodChannel) {

    val searchProgressObservable = ObservableBoolean(false)
    var selectedMedia: Media? = null

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

    fun getAudioUrl(id: String) {
        methodChannel.invokeMethod(INVOKE.AUDIO_URL.invoke, id)
    }


}