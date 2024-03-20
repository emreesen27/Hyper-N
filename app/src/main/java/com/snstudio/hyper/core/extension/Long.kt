package com.snstudio.hyper.core.extension

import java.util.concurrent.TimeUnit

fun Long.toDuration(): String {
    val hours = TimeUnit.MILLISECONDS.toHours(this)
    val minutes = TimeUnit.MILLISECONDS.toMinutes(this) % 60
    val seconds = TimeUnit.MILLISECONDS.toSeconds(this) % 60
    return String.format("%02d:%02d:%02d", hours, minutes, seconds)
}