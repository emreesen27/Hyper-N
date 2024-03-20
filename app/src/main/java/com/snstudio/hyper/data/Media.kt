package com.snstudio.hyper.data

data class Media(
    val id: String,
    val title: String,
    val description: String,
    val author: String,
    val url: String,
    val duration: Long,
    val thumbnail: String,
    val publishYear: String?,
    val uploadYear: String?,
    val viewCount: String,
    val type: Int
)
