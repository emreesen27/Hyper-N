package com.snstudio.hyper.core.extension

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.target.Target
import com.bumptech.glide.request.transition.Transition
import org.json.JSONException
import org.json.JSONObject

fun String.convertToBitmap(
    context: Context,
    onSuccess: (Bitmap) -> Unit,
    onFailure: () -> Unit,
) {
    Glide.with(context)
        .asBitmap()
        .load(this)
        .into(
            object : CustomTarget<Bitmap>() {
                override fun onResourceReady(
                    resource: Bitmap,
                    transition: Transition<in Bitmap>?,
                ) {
                    onSuccess(resource)
                }

                override fun onLoadFailed(errorDrawable: Drawable?) {
                    super.onLoadFailed(errorDrawable)
                    onFailure()
                }

                override fun onLoadCleared(placeholder: Drawable?) {}
            },
        )
}

fun String?.parseObject(name: String): String? {
    if (this == null) return null
    return try {
        val jsonObject = JSONObject(this)
        jsonObject.getString(name)
    } catch (e: JSONException) {
        e.printStackTrace()
        null
    }
}

fun String?.isValidImageUrl(
    context: Context,
    onResult: (Boolean) -> Unit,
) {
    if (this == null) {
        onResult(false)
        return
    }

    Glide.with(context)
        .load(this)
        .listener(
            object : RequestListener<Drawable> {
                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: Target<Drawable>,
                    isFirstResource: Boolean,
                ): Boolean {
                    onResult(false)
                    return false
                }

                override fun onResourceReady(
                    resource: Drawable,
                    model: Any,
                    target: Target<Drawable>?,
                    dataSource: DataSource,
                    isFirstResource: Boolean,
                ): Boolean {
                    onResult(true)
                    return false
                }
            },
        )
        .preload()
}
