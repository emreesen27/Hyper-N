package com.snstudio.hyper.data.model

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation

data class PlaylistWithMedia(
    @Embedded val playlist: Playlist,
    @Relation(
        parentColumn = "playlistId",
        entityColumn = "id",
        associateBy = Junction(PlaylistMediaCrossRef::class)
    )
    val mediaList: List<Media>
)