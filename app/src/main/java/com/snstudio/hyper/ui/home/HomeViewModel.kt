package com.snstudio.hyper.ui.home

import com.snstudio.hyper.core.base.BaseViewModel
import com.snstudio.hyper.util.PrefsTag
import com.snstudio.hyper.util.RECEIVED
import com.snstudio.hyper.util.SharedPreferenceManager
import dagger.hilt.android.lifecycle.HiltViewModel
import io.flutter.plugin.common.MethodChannel
import javax.inject.Inject


@HiltViewModel
class HomeViewModel @Inject constructor(
    private val methodChannel: MethodChannel,
    private val sharedPreferenceManager: SharedPreferenceManager
) : BaseViewModel(methodChannel) {

    var notificationRuntimeRequested: Boolean = false

    init {
        receivedData(RECEIVED.HIGHLIGHTS_RECEIVED.received)
    }

    fun hasNotificationRequestedPermissionBefore() =
        sharedPreferenceManager.getBoolean(PrefsTag.PERMISSION_NOTIFICATION)

    fun setNotificationPermissionRequested() =
        sharedPreferenceManager.putBoolean(PrefsTag.PERMISSION_NOTIFICATION, true)

}