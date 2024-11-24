package com.snstudio.hyper.core.extension

import android.animation.ValueAnimator
import android.view.View
import android.view.ViewPropertyAnimator
import android.view.animation.AccelerateInterpolator
import android.view.animation.DecelerateInterpolator
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.interpolator.view.animation.FastOutSlowInInterpolator
import com.snstudio.hyper.R
import com.snstudio.hyper.util.SafeClickListener

fun View.visible() {
    this.visibility = View.VISIBLE
}

fun View.gone() {
    this.visibility = View.GONE
}

fun View.slideDownToggle(
    show: Boolean,
    duration: Long = 300,
) {
    if (show) {
        if (isVisible) return
        visibility = View.VISIBLE
        alpha = 0f
        translationY = height.toFloat()
    }

    animate().translationY(if (show) 0f else height.toFloat())
        .alpha(if (show) 1f else 0f)
        .setInterpolator(if (show) DecelerateInterpolator() else AccelerateInterpolator())
        .setDuration(duration)
        .withEndAction {
            if (!show) {
                visibility = View.GONE
                translationY = 0f
            }
        }.start()
}

fun View.slideOutDown(): ViewPropertyAnimator {
    return animate().translationY(height.toFloat()).alpha(0F).setDuration(300)
        .setInterpolator(FastOutSlowInInterpolator())
}

fun View.slideInUp(): ViewPropertyAnimator {
    return animate().translationY(0f).alpha(1f).setDuration(300)
        .setInterpolator(FastOutSlowInInterpolator())
}

fun View.startColorAnimation(): ValueAnimator {
    val colors =
        intArrayOf(
            resources.getColor(R.color.main_color, null),
            resources.getColor(R.color.old_main_color_mid, null),
            resources.getColor(R.color.purple_700, null),
            resources.getColor(R.color.purple_500, null),
        )

    val colorAnimator =
        ValueAnimator.ofArgb(*colors).apply {
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
