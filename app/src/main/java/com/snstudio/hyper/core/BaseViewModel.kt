package com.snstudio.hyper.core

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import javax.inject.Inject


open class BaseViewModel(private val methodChannel: MethodChannel) : ViewModel() {

    private val _receivedData: MutableLiveData<MethodCall> = MutableLiveData()
    val receivedData: MutableLiveData<MethodCall> = _receivedData

    fun receivedData(methodName: String) {
        methodChannel.setMethodCallHandler { call, result ->
            if (call.method == methodName) {
                _receivedData.value = call
                result.success(null)
            } else {
                result.notImplemented()
            }
        }
    }

}