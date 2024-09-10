package com.snstudio.hyper.core.extension

import android.graphics.Bitmap
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.snstudio.hyper.R

@BindingAdapter("load")
fun ImageView.loadWithGlide(path: Any) {
    Glide.with(context)
        .load(path)
        .centerCrop()
        // .fitCenter()
        .placeholder(R.drawable.layer_placeholder)
        .error(R.drawable.layer_placeholder) // change err
        .diskCacheStrategy(DiskCacheStrategy.RESOURCE).into(this)
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
