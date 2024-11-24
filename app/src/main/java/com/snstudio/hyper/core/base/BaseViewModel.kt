package com.snstudio.hyper.core.base

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

open class BaseViewModel(private val methodChannel: MethodChannel) : ViewModel() {
    private val _receivedData = MutableSharedFlow<MethodCall>()
    val receivedData = _receivedData.asSharedFlow()

    fun receivedData(vararg methodNames: String) {
        methodChannel.setMethodCallHandler { call, result ->
            if (methodNames.contains(call.method)) {
                viewModelScope.launch {
                    _receivedData.emit(call)
                }
                result.success(null)
            } else {
                result.notImplemented()
            }
        }
    }
}
