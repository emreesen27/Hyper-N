package com.snstudio.hyper.shared

import android.app.Application
import android.content.ComponentName
import androidx.annotation.OptIn
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
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
    val playerLiveData: LiveData<Player> = _playerLiveData

    private val _playerStatusLiveData = MutableLiveData<Boolean>()
    val playerStatusLiveData: LiveData<Boolean> = _playerStatusLiveData

    private val _metaDataLiveData = MutableLiveData<MediaMetadata>()
    val metaDataLiveData: LiveData<MediaMetadata> = _metaDataLiveData

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
            initPlayerListener()
        }, MoreExecutors.directExecutor())
    }

    private fun initPlayerListener() {
        player.addListener(
            object : Player.Listener {
                override fun onIsPlayingChanged(isPlaying: Boolean) {
                    _playerStatusLiveData.postValue(isPlaying)
                }

                override fun onMediaMetadataChanged(mediaMetadata: MediaMetadata) {
                    _metaDataLiveData.postValue(mediaMetadata)
                }
            }
        )
    }

    fun playMediaItem(item: MediaItem) {
        player.setMediaItem(item)
        player.prepare()
        player.play()
    }

    fun stopPlayer() {
        player.stop()
    }

}