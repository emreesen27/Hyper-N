package com.snstudio.hyper.data

import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata

class MediaItemBuilder {
    private var mediaId: String? = null
    private var mediaTitle: String? = null

    fun setMediaId(mediaId: String): MediaItemBuilder {
        this.mediaId = mediaId
        return this
    }

    fun setMediaTitle(title: String): MediaItemBuilder {
        this.mediaTitle = title
        return this
    }

    fun build(): MediaItem {
        val id = requireNotNull(mediaId) { "Media ID must be set" }
        val title = requireNotNull(mediaTitle) { "Media title ID must be set" }
        val metaData = MediaMetadata.Builder().setTitle(title).build()
        return MediaItem.Builder()
            .setMediaId(id)
            .setMediaMetadata(metaData)
            .build()
    }
}