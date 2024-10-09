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

    private val playerMLiveData = MutableLiveData<Player>()
    val playerLiveData: LiveData<Player> = playerMLiveData

    private val playbackStateMLiveData = MutableLiveData<Unit>()
    val playbackStateLiveData: LiveData<Unit> = playbackStateMLiveData

    private val metaDataMLiveData = MutableLiveData<MediaMetadata>()
    val metaDataLiveData: LiveData<MediaMetadata> = metaDataMLiveData

    private val playerWhenReadyMLiveData = MutableLiveData<Boolean>()
    val playerWhenReadyLiveData: LiveData<Boolean> = playerWhenReadyMLiveData

    private val showPlayerMenuMLiveData = MutableLiveData<Boolean>()
    val showPlayerMenuLiveData: LiveData<Boolean> = showPlayerMenuMLiveData

    private val playlist = mutableListOf<MediaItem>()

    init {
        initMediaController()
    }

    @OptIn(UnstableApi::class)
    private fun initMediaController() {
        val sessionToken =
            SessionToken(
                getApplication(),
                ComponentName(getApplication(), MusicPlayerService::class.java),
            )
        mediaControllerFuture = MediaController.Builder(getApplication(), sessionToken).buildAsync()
        mediaControllerFuture.addListener({
            player = mediaControllerFuture.get()
            playerMLiveData.value = player
            initPlayerListener()
        }, MoreExecutors.directExecutor())
    }

    private fun initPlayerListener() {
        player.addListener(
            object : Player.Listener {
                override fun onMediaMetadataChanged(mediaMetadata: MediaMetadata) {
                    metaDataMLiveData.postValue(mediaMetadata)
                }

                override fun onPlayWhenReadyChanged(
                    playWhenReady: Boolean,
                    reason: Int,
                ) {
                    playerWhenReadyMLiveData.postValue(playWhenReady)
                }

                override fun onPlaybackStateChanged(playbackState: Int) {
                    /*
                    if (playbackState == Player.STATE_READY) {
                        playbackStateMLiveData.postValue(Unit)
                    }*/
                }
            },
        )
    }

    fun getCurrentMediaUrl(): String = player.currentMediaItem?.localConfiguration?.uri.toString()

    fun getMediaMetaData(): MediaMetadata = player.mediaMetadata

    fun showPLayerMenu(show: Boolean) {
        showPlayerMenuMLiveData.postValue(show)
    }

    fun playMediaItem(item: MediaItem) {
        player.setMediaItem(item)
        player.prepare()
        player.play()
    }

    fun stopPlayer() {
        player.stop()
    }

    fun setPlaylist(
        mediaItems: List<MediaItem>,
        startPosition: Int = 0,
    ) {
        playlist.clear()
        playlist.addAll(mediaItems)
        player.setMediaItems(mediaItems)
        player.prepare()
        player.seekTo(startPosition, 0)
        player.play()
    }
}
