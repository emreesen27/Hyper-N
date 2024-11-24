package com.snstudio.hyper.shared

import android.app.Application
import android.content.ComponentName
import androidx.annotation.OptIn
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import com.google.common.util.concurrent.ListenableFuture
import com.google.common.util.concurrent.MoreExecutors
import com.snstudio.hyper.service.MusicPlayerService
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class MediaViewModel(application: Application) : AndroidViewModel(application) {
    private lateinit var player: Player
    private lateinit var mediaControllerFuture: ListenableFuture<MediaController>

    private val playerMLiveData = MutableLiveData<Player>()
    val playerLiveData: LiveData<Player> = playerMLiveData

    private val metaDataMLiveData = MutableLiveData<MediaMetadata>()
    val metaDataLiveData: LiveData<MediaMetadata> = metaDataMLiveData

    private val showPlayerMenuMLiveData = MutableLiveData<Boolean>()
    val showPlayerMenuLiveData: LiveData<Boolean> = showPlayerMenuMLiveData

    private val progressMLiveData = MutableLiveData<Int>()
    val progressLiveData: LiveData<Int> = progressMLiveData

    private var progressUpdateJob: Job? = null

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
                    if (playWhenReady) {
                        startProgressUpdate()
                    } else {
                        stopProgressUpdate()
                    }
                }

                override fun onPlaybackStateChanged(playbackState: Int) {
                    when (playbackState) {
                        Player.STATE_READY -> {
                            startProgressUpdate()
                        }

                        Player.STATE_ENDED, Player.STATE_IDLE -> {
                            stopProgressUpdate()
                        }

                        else -> return
                    }
                }
            },
        )
    }

    private fun startProgressUpdate() {
        progressUpdateJob?.cancel()
        progressUpdateJob =
            viewModelScope.launch {
                while (isActive) {
                    updateProgress()
                    delay(1000)
                }
            }
    }

    private fun stopProgressUpdate() {
        progressUpdateJob?.cancel()
        progressUpdateJob = null
    }

    private fun updateProgress() {
        val duration = player.duration
        val currentPosition = player.currentPosition

        if (duration > 0) {
            val progress = ((currentPosition.toFloat() / duration.toFloat()) * 100).toInt()
            progressMLiveData.postValue(progress)
        }
    }

    fun showPLayerMenu(show: Boolean) {
        showPlayerMenuMLiveData.postValue(show)
    }

    fun playMediaItem(item: MediaItem) {
        player.setMediaItem(item)
        player.prepare()
        player.play()
    }

    fun releasePLayer() {
        player.release()
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
