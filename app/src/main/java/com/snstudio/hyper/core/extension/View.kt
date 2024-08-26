package com.snstudio.hyper.core.extension

import android.animation.ValueAnimator
import android.view.View
import androidx.core.content.ContextCompat
import com.snstudio.hyper.R
import com.snstudio.hyper.util.SafeClickListener

fun View.visible() {
    this.visibility = View.VISIBLE
}

fun View.gone() {
    this.visibility = View.GONE
}

fun View.invisible() {
    this.visibility = View.INVISIBLE
}

fun View.visibility(visibility: Boolean) {
    if (visibility) this.visible() else this.gone()
}

fun View.startColorAnimation(): ValueAnimator {
    val colors = intArrayOf(
        resources.getColor(R.color.main_color, null),
        resources.getColor(R.color.main_color_mid, null),
        resources.getColor(R.color.purple_700, null),
        resources.getColor(R.color.purple_500, null),
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
    return colorAnimator
}

fun View.stopColorAnimation(animator: ValueAnimator?) {
    this.setBackgroundColor(ContextCompat.getColor(context, R.color.background_color))
    animator?.cancel()
}

fun View.click(onClick: (View) -> Unit) {
    val safeClickListener =
        SafeClickListener {
            onClick(it)
        }
    setOnClickListener(safeClickListener)
}