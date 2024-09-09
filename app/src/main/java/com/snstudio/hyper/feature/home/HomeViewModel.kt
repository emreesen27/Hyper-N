package com.snstudio.hyper.feature.home

import androidx.databinding.ObservableBoolean
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.snstudio.hyper.BuildConfig
import com.snstudio.hyper.core.base.BaseViewModel
import com.snstudio.hyper.core.extension.parseObject
import com.snstudio.hyper.util.PrefsTag
import com.snstudio.hyper.util.SharedPreferenceManager
import dagger.hilt.android.lifecycle.HiltViewModel
import io.flutter.plugin.common.MethodChannel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import okio.IOException
import javax.inject.Inject


@HiltViewModel
class HomeViewModel @Inject constructor(
    methodChannel: MethodChannel,
    private val sharedPreferenceManager: SharedPreferenceManager
) : BaseViewModel(methodChannel) {

    private val _forceUpdateLiveData = MutableLiveData<Boolean>()
    val forceUpdateLiveData: LiveData<Boolean> = _forceUpdateLiveData

    var notificationRuntimeRequested: Boolean = false

    val progress = ObservableBoolean(true)

    init {
        checkVersion()
    }

    fun hasNotificationRequestedPermissionBefore() =
        sharedPreferenceManager.getBoolean(PrefsTag.PERMISSION_NOTIFICATION)

    fun setNotificationPermissionRequested() =
        sharedPreferenceManager.putBoolean(PrefsTag.PERMISSION_NOTIFICATION, true)

    private fun checkVersion() = viewModelScope.launch {
        val latestVersion = fetchLatestVersion()
        _forceUpdateLiveData.postValue(latestVersion != BuildConfig.VERSION_NAME)
        progress.set(false)
    }

    private suspend fun fetchLatestVersion(): String? = withContext(Dispatchers.IO) {
        val client = OkHttpClient()
        val request = Request.Builder()
            .url(BuildConfig.RELEASE_VERSION)
            .build()

        return@withContext try {
            val response = client.newCall(request).execute()
            if (response.isSuccessful) {
                val jsonResponse = response.body?.string()
                jsonResponse.parseObject(JSON_TAG_NAME)
            } else {
                null
            }
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }
    }

    companion object {
        const val JSON_TAG_NAME = "tag_name"
    }

}