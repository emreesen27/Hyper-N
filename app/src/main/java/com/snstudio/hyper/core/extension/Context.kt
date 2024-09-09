package com.snstudio.hyper.core.extension

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import es.dmoral.toasty.Toasty

fun Context.startActivitySafely(intent: Intent) {
    try {
        startActivity(intent)
    } catch (e: ActivityNotFoundException) {
        e.printStackTrace()
    }
}

fun Context.waningToast(msg: String) {
    Toasty.warning(this, msg, Toast.LENGTH_SHORT, true).show();
}

fun Context.infoToast(msg: String) {
    Toasty.info(this, msg, Toast.LENGTH_SHORT, true).show();
}

fun Context.openUrlInBrowser(url: String) {
    val intent = Intent(Intent.ACTION_VIEW).apply {
        data = Uri.parse(url)
    }
    startActivity(intent)
}