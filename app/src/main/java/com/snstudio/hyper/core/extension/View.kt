package com.snstudio.hyper.core.extension

import android.view.View

fun View.visible() {
    this.visibility = View.VISIBLE
}

fun View.gone() {
    this.visibility = View.GONE
}

fun View.visibility(visibility: Boolean) {
    if (visibility) this.visible() else this.gone()
}