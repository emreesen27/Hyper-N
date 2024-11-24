package com.snstudio.hyper.core.extension

import android.graphics.Bitmap
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import androidx.media3.common.MediaMetadata
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.snstudio.hyper.R

@BindingAdapter("load")
fun ImageView.loadWithGlide(path: Any) {
    Glide.with(context)
        .load(path)
        .centerCrop()
        .placeholder(R.drawable.layer_placeholder)
        .error(R.drawable.layer_placeholder) // change err
        .diskCacheStrategy(DiskCacheStrategy.RESOURCE).into(this)
}

fun ImageView.loadWithGlideWithRadius(path: Any) {
    Glide.with(context)
        .load(path)
        .transform(CenterCrop(), RoundedCorners(12))
        .placeholder(R.drawable.ic_placeholder_new)
        .error(R.drawable.ic_placeholder_new).into(this)
}

@BindingAdapter("loadBitmap")
fun ImageView.loadBitmapWithGlide(path: Bitmap) {
    Glide.with(context)
        .load(path)
        .centerCrop()
        // .fitCenter()
        .placeholder(R.drawable.layer_placeholder)
        .error(R.drawable.layer_placeholder)
        .diskCacheStrategy(DiskCacheStrategy.NONE).into(this)
}

fun ImageView.loadGif(image: Int) {
    Glide.with(context)
        .asGif()
        .load(image)
        .into(this)
}

fun ImageView.loadArtwork(data: MediaMetadata) {
    data.artworkUri?.let { loadWithGlide(it) }
        ?: data.artworkData?.let { loadWithGlide(it) }
}
