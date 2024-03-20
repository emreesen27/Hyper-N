package com.snstudio.hyper.ui.home

import androidx.databinding.ObservableBoolean
import com.snstudio.hyper.util.RECEIVED
import com.snstudio.hyper.core.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import io.flutter.plugin.common.MethodChannel
import javax.inject.Inject


@HiltViewModel
class HomeViewModel @Inject constructor(
    private val methodChannel: MethodChannel
) : BaseViewModel(methodChannel) {

    val highlightsProgress: ObservableBoolean = ObservableBoolean(false)

    init {
        getHighlights()
        receivedData(RECEIVED.HIGHLIGHTS_RECEIVED.received)
    }

    private fun getHighlights() {
        highlightsProgress.set(true)
        methodChannel.invokeMethod(
            "highlights",
            listOf(
                "srPnX2p05jc",
                "SiMPOxBOy_4",
                "VREnTCTeS4k",
                "9TSf2k03HPA",
                "TJ8ADu6MfGo",
                "OFWBSpsqYM8",
                "1X0LU8GTj70",
            )
        )
    }

}