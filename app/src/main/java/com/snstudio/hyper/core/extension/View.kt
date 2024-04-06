package com.snstudio.hyper.core.extension

import android.animation.ValueAnimator
import android.view.View
import com.snstudio.hyper.R

fun View.visible() {
    this.visibility = View.VISIBLE
}

fun View.gone() {
    this.visibility = View.GONE
}

fun View.visibility(visibility: Boolean) {
    if (visibility) this.visible() else this.gone()
}

fun View.startColorAnimation() {
    val colors = intArrayOf(
        resources.getColor(R.color.main_color_light, null),
        resources.getColor(R.color.main_color, null),
        resources.getColor(R.color.main_color_dark, null),
    )

    val colorAnimator = ValueAnimator.ofArgb(*colors).apply {
        duration = 5000
        repeatCount = ValueAnimator.INFINITE
        repeatMode = ValueAnimator.REVERSE
        addUpdateListener { animator ->
            val animatedValue = animator.animatedValue as Int
            setBackgroundColor(animatedValue)
        }
    }

    colorAnimator.start()
}