package com.snstudio.hyper.data.model

import androidx.room.Entity

@Entity(primaryKeys = ["playlistId", "id"])
data class PlaylistMediaCrossRef(
    val playlistId: Long,
    val id: String
)

