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
import com.snstudio.hyper.data.model.Media
import com.snstudio.hyper.service.MusicPlayerService

class MediaViewModel(application: Application) : AndroidViewModel(application) {
    private lateinit var player: Player
    private lateinit var mediaControllerFuture: ListenableFuture<MediaController>

    private val _playerLiveData = MutableLiveData<Player>()
    val playerLiveData: LiveData<Player> = _playerLiveData

    private val _playbackStateLiveData = MutableLiveData<Boolean>()
    val playbackStateLiveData: LiveData<Boolean> = _playbackStateLiveData

    private val _metaDataLiveData = MutableLiveData<MediaMetadata>()
    val metaDataLiveData: LiveData<MediaMetadata> = _metaDataLiveData

    private val _playerWhenReadyLiveData = MutableLiveData<Boolean>()
    val playerWhenReadyLiveData: LiveData<Boolean> = _playerWhenReadyLiveData

    private val _currentMediaLiveData = MutableLiveData<Media>()
    val currentMediaLiveData: LiveData<Media> = _currentMediaLiveData

    private val playlist = mutableListOf<MediaItem>()

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

                override fun onMediaMetadataChanged(mediaMetadata: MediaMetadata) {
                    _metaDataLiveData.postValue(mediaMetadata)
                }

                override fun onPlayWhenReadyChanged(playWhenReady: Boolean, reason: Int) {
                    _playerWhenReadyLiveData.postValue(playWhenReady)
                }

                override fun onPlaybackStateChanged(playbackState: Int) {
                    if (playbackState == Player.STATE_READY) {
                        _playbackStateLiveData.postValue(!_currentMediaLiveData.value?.localPath.isNullOrEmpty())
                    }
                }
            }
        )
    }

    fun getCurrentMediaUrl(): String =
        player.currentMediaItem?.localConfiguration?.uri.toString()

    fun getMediaMetaData(): MediaMetadata = player.mediaMetadata

    fun playMediaItem(item: MediaItem) {
        player.setMediaItem(item)
        player.prepare()
        player.play()
    }

    fun stopPlayer() {
        player.stop()
    }

    fun setCurrentMedia(media: Media) {
        _currentMediaLiveData.value = media
    }

    fun setPlaylist(mediaItems: List<MediaItem>) {
        playlist.clear()
        playlist.addAll(mediaItems)
        player.setMediaItems(mediaItems)
        player.prepare()
        player.play()
    }
}