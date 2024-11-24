package com.snstudio.hyper.feature.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.snstudio.hyper.BuildConfig
import com.snstudio.hyper.core.base.BaseViewModel
import com.snstudio.hyper.core.extension.parseObject
import com.snstudio.hyper.data.local.repository.MediaRepository
import com.snstudio.hyper.data.model.Media
import com.snstudio.hyper.util.INVOKE
import com.snstudio.hyper.util.PrefsTag
import com.snstudio.hyper.util.RECEIVED
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
class HomeViewModel
    @Inject
    constructor(
        private val methodChannel: MethodChannel,
        private val sharedPreferenceManager: SharedPreferenceManager,
        private val localMediaRepository: MediaRepository,
    ) : BaseViewModel(methodChannel) {
        private val localMediaListMLiveData = MutableLiveData<List<Media>>()
        val localMediaListLiveData: LiveData<List<Media>> = localMediaListMLiveData

        private val forceUpdateMLiveData = MutableLiveData<String?>()
        val forceUpdateLiveData: LiveData<String?> = forceUpdateMLiveData

        var notificationRuntimeRequested: Boolean = false
        var currentMedia: Media? = null
            private set

        var highlightsInitialized: Boolean = false

        var isVersionDialogShown: Boolean = false

        init {
            collectLocalMediaData()
            checkVersion()
        }

        fun initReceive() {
            receivedData(RECEIVED.HIGHLIGHTS_RECEIVED.received, RECEIVED.AUDIO_URL_RECEIVED.received)
        }

        fun hasNotificationRequestedPermissionBefore() = sharedPreferenceManager.getBoolean(PrefsTag.PERMISSION_NOTIFICATION)

        fun setNotificationPermissionRequested() = sharedPreferenceManager.putBoolean(PrefsTag.PERMISSION_NOTIFICATION, true)

        private fun collectLocalMediaData() =
            viewModelScope.launch {
                localMediaRepository.localMediaList
                    .collect {
                        localMediaListMLiveData.value = it.asReversed().take(6)
                    }
            }

        fun invokeAudioUrl(media: Media) {
            currentMedia = media
            methodChannel.invokeMethod(INVOKE.AUDIO_URL.invoke, media.id)
        }

        private fun checkVersion() =
            viewModelScope.launch {
                val latestVersion = fetchLatestVersion()
                forceUpdateMLiveData.postValue(latestVersion)
            }

        private suspend fun fetchLatestVersion(): String? =
            withContext(Dispatchers.IO) {
                val client = OkHttpClient()
                val request =
                    Request.Builder()
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

        fun getHighlights() {
            methodChannel.invokeMethod(
                INVOKE.HIGHLIGHTS.invoke,
                listOf(
                    "SiMPOxBOy_4",
                    "VREnTCTeS4k",
                    "9TSf2k03HPA",
                    "TJ8ADu6MfGo",
                    "OFWBSpsqYM8",
                    "1X0LU8GTj70",
                ),
            )
        }

        companion object {
            const val JSON_TAG_NAME = "tag_name"
        }
    }
