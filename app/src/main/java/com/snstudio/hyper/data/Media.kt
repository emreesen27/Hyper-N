package com.snstudio.hyper.data

import android.graphics.Bitmap
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "media")
data class Media(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: String,
    @ColumnInfo(name = "title")
    val title: String,
    @ColumnInfo(name = "description")
    val description: String?,
    @ColumnInfo(name = "author")
    val author: String?,
    @ColumnInfo(name = "url")
    val url: String?,
    @ColumnInfo(name = "duration")
    val duration: Long?,
    @ColumnInfo(name = "thumbnail")
    val thumbnail: String?,
    @ColumnInfo(name = "publishYear")
    val publishYear: String?,
    @ColumnInfo(name = "uploadYear")
    val uploadYear: String?,
    @ColumnInfo(name = "viewCount")
    val viewCount: String?,
    @ColumnInfo(name = "type")
    val type: Int,
    @ColumnInfo(name = "bitmap")
    val bitmap: Bitmap?,
    @ColumnInfo
    val localPath: String?
)
