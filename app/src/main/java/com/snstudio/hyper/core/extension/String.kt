package com.snstudio.hyper.core.extension

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition

fun String.convertToBitmap(context: Context, onSuccess: (Bitmap) -> Unit, onFailure: () -> Unit) {
    Glide.with(context)
        .asBitmap()
        .load(this)
        .into(object : CustomTarget<Bitmap>() {
            override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                onSuccess(resource)
            }

            override fun onLoadFailed(errorDrawable: Drawable?) {
                super.onLoadFailed(errorDrawable)
                onFailure()
            }

            override fun onLoadCleared(placeholder: Drawable?) {}
        })
}