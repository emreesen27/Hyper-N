package com.snstudio.hyper

import com.snstudio.hyper.core.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import io.flutter.plugin.common.MethodChannel
import javax.inject.Inject


@HiltViewModel
class SearchViewModel @Inject constructor(
    private val methodChannel: MethodChannel
) : BaseViewModel(methodChannel) {

    init {
        search("emre")
        receivedData(MethodName.SEARCH_RECEIVED)
    }

    private fun search(query: String) {
        methodChannel.invokeMethod(MethodName.SEARCH, query)
    }

    fun nextPage() {
        methodChannel.invokeMethod("nextPage", null)
    }


}


/*
*
*
    private fun a() {
        methodChannel.setMethodCallHandler { call, result ->
            if (call.method == "receiveSearchData") {
                val a = call.argument<List<HashMap<String, String>>>("data")
                println("emre${a?.first()}")
                result.success(null)
            } else if (call.method == "receiveAudioUrl") {
                val a = call.argument<String>("data")
                println("emre$a")
            } else {
                println("implemente")
                result.notImplemented()
            }
        }
    }
* */