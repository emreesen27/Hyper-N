package com.snstudio.hyper.service

import android.app.PendingIntent
import android.content.Intent
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.DefaultRenderersFactory
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.MediaLibraryService
import androidx.media3.session.MediaSession
import com.google.common.util.concurrent.Futures
import com.google.common.util.concurrent.ListenableFuture
import com.snstudio.hyper.MainActivity

@UnstableApi
class MusicPlayerService : MediaLibraryService() {
    private lateinit var player: Player
    private lateinit var session: MediaLibrarySession

    override fun onCreate() {
        super.onCreate()

        player =
            ExoPlayer.Builder(applicationContext)
                .setRenderersFactory(
                    DefaultRenderersFactory(this).setExtensionRendererMode(
                        DefaultRenderersFactory.EXTENSION_RENDERER_MODE_PREFER,
                    ),
                ).build()

        val notificationIntent =
            Intent(this, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            }

        val pendingIntent =
            PendingIntent.getActivity(
                this,
                0,
                notificationIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
            )

        session =
            MediaLibrarySession.Builder(
                this, player,
                object : MediaLibrarySession.Callback {
                    override fun onAddMediaItems(
                        mediaSession: MediaSession,
                        controller: MediaSession.ControllerInfo,
                        mediaItems: MutableList<MediaItem>,
                    ): ListenableFuture<MutableList<MediaItem>> {
                        val updatedMediaItems =
                            mediaItems.map { it.buildUpon().setUri(it.mediaId).build() }
                                .toMutableList()
                        return Futures.immediateFuture(updatedMediaItems)
                    }
                },
            ).setSessionActivity(pendingIntent).build()
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        super.onTaskRemoved(rootIntent)
        stopSelf()
        stopForeground(STOP_FOREGROUND_REMOVE)
    }

    override fun onGetSession(controllerInfo: MediaSession.ControllerInfo): MediaLibrarySession {
        return session
    }

    override fun onDestroy() {
        super.onDestroy()
        player.release()
        session.release()
    }
}
