package com.snstudio.hyper.data.model

import android.graphics.Bitmap
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "media")
data class Media(
    @PrimaryKey
    val id: String,
    val title: String,
    val description: String?,
    val author: String?,
    val url: String?,
    val duration: Long?,
    val thumbnail: String?,
    val publishYear: String?,
    val uploadYear: String?,
    val viewCount: String?,
    val type: Int,
    val bitmap: Bitmap?,
    val localPath: String?
)
