package com.snstudio.hyper.data

import android.graphics.Bitmap
import android.net.Uri
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import java.io.ByteArrayOutputStream

class MediaItemBuilder {
    private var mediaId: String? = null
    private var mediaTitle: String? = null
    private var artworkUri: Uri? = null
    private var artworkBitmap: Bitmap? = null

    fun setMediaId(mediaId: String): MediaItemBuilder {
        this.mediaId = mediaId
        return this
    }

    fun setMediaTitle(title: String): MediaItemBuilder {
        this.mediaTitle = title
        return this
    }

    fun setArtWorkUrl(url: String): MediaItemBuilder {
        this.artworkUri = Uri.parse(url)
        this.artworkBitmap = null
        return this
    }

    fun setArtWorkBitmap(bitmap: Bitmap?): MediaItemBuilder {
        this.artworkBitmap = bitmap
        this.artworkUri = null
        return this
    }

    fun build(): MediaItem {
        val id = requireNotNull(mediaId) { "Media ID must be set" }
        val title = requireNotNull(mediaTitle) { "Media title must be set" }

        val metadataBuilder =
            MediaMetadata.Builder()
                .setTitle(title)

        artworkUri?.let { uri ->
            metadataBuilder.setArtworkUri(uri)
        }

        artworkBitmap?.let { bitmap ->
            val stream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
            metadataBuilder.setArtworkData(
                stream.toByteArray(),
                MediaMetadata.PICTURE_TYPE_FRONT_COVER,
            )
        }

        return MediaItem.Builder()
            .setMediaId(id)
            .setMediaMetadata(metadataBuilder.build())
            .build()
    }
}
