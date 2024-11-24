package com.snstudio.hyper.util

const val DATA_KEY = "data"

enum class RECEIVED(val received: String) {
    SEARCH_RECEIVED("receiveSearchData"),
    NEXT_RECEIVED("receiveNextSearchData"),
    AUDIO_URL_RECEIVED("receiveAudioUrl"),
    HIGHLIGHTS_RECEIVED("receiveHighlightsData"),
}

enum class INVOKE(val invoke: String) {
    SEARCH("search"),
    AUDIO_URL("getAudioUrl"),
    NEXT("nextPage"),
    HIGHLIGHTS("highlights"),
}

enum class EXCEPTION(val code: String) {
    YT_EXPLODE_EXCEPTION("403"),
    UNEXPECTED("0"),
}
