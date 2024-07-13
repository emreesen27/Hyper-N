package com.snstudio.hyper.core.extension

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent

fun Context.startActivitySafely(intent: Intent) {
    try {
        startActivity(intent)
    } catch (e: ActivityNotFoundException) {
        e.printStackTrace()
    }
}