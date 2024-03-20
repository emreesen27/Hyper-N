package com.snstudio.hyper.util


const val DATA_KEY = "data"

enum class RECEIVED(val received: String) {
    SEARCH_RECEIVED("receiveSearchData"),
    NEXT_RECEIVED("receiveNextSearchData"),
    HIGHLIGHTS_RECEIVED("receiveHighlightsData"),
    AUDIO_URL_RECEIVED("receiveAudioUrl")
}

enum class INVOKE(val invoke: String) {
    SEARCH("search"),
    AUDIO_URL("getAudioUrl"),
    NEXT("nextPage")
}