package com.snstudio.hyper.shared

import android.app.Application
import android.content.ComponentName
import android.net.Uri
import androidx.annotation.OptIn
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import com.google.common.util.concurrent.ListenableFuture
import com.google.common.util.concurrent.MoreExecutors
import com.snstudio.hyper.service.MusicPlayerService

class MediaViewModel(application: Application) : AndroidViewModel(application) {
    private lateinit var player: Player
    private lateinit var mediaControllerFuture: ListenableFuture<MediaController>

    private val _playerLiveData = MutableLiveData<Player>()
    val playerLiveData: LiveData<Player>
        get() = _playerLiveData

    private val _playerVisibleLiveData = MutableLiveData<Boolean>()
    val playerVisibleLiveData: LiveData<Boolean>
        get() = _playerVisibleLiveData

    init {
        initMediaController()
    }

    @OptIn(UnstableApi::class)
    private fun initMediaController() {
        val sessionToken = SessionToken(
            getApplication(),
            ComponentName(getApplication(), MusicPlayerService::class.java)
        )
        mediaControllerFuture = MediaController.Builder(getApplication(), sessionToken).buildAsync()
        mediaControllerFuture.addListener({
            player = mediaControllerFuture.get()
            _playerLiveData.value = player
        }, MoreExecutors.directExecutor())
    }


    fun playMediaItem(uri: Uri) {
        val newItem = MediaItem.Builder().setMediaId("$uri").build()
        player.setMediaItem(newItem)
        player.prepare()
        player.play()
        setPlayerVisibility(true)
    }

    fun releasePlayer() {
        player.release()
        setPlayerVisibility(false)
    }

    private fun setPlayerVisibility(visibility: Boolean) {
        _playerVisibleLiveData.value = visibility
    }

}