package com.snstudio.hyper.core.base

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel

open class BaseViewModel(private val methodChannel: MethodChannel) : ViewModel() {
    private val _receivedData: MutableLiveData<MethodCall> = MutableLiveData()
    val receivedData: MutableLiveData<MethodCall> = _receivedData

    fun receivedData(vararg methodNames: String) {
        methodChannel.setMethodCallHandler { call, result ->
            if (methodNames.contains(call.method)) {
                _receivedData.value = call
                result.success(null)
            } else {
                result.notImplemented()
            }
        }
    }
}
