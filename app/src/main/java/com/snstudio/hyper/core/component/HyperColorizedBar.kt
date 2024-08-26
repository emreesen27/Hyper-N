package com.snstudio.hyper.core.component

import android.animation.ArgbEvaluator
import android.animation.ValueAnimator
import android.app.Activity
import android.content.Context
import android.content.res.TypedArray
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textview.MaterialTextView
import com.snstudio.hyper.R

class HyperColorizedBar @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {

    private var activity: Activity? = null
    private var onIconClick: ((Int) -> Unit)? = null
    private var currentBackgroundColor: Int =
        ContextCompat.getColor(context, R.color.background_color)
    private val titleView: MaterialTextView
    private val iconContainer: LinearLayout

    init {
        LayoutInflater.from(context).inflate(R.layout.layout_hyper_colorized_bar, this, true)
        titleView = findViewById(R.id.title)
        iconContainer = findViewById(R.id.iconContainer)
        initAttributes(attrs)
    }

    fun setOnIconClickListener(listener: (Int) -> Unit) {
        onIconClick = listener
    }

    fun setActivity(activity: Activity) {
        this.activity = activity
    }

    fun setTitle(title: String) {
        titleView.text = title
    }

    private fun initAttributes(attrs: AttributeSet?) {
        context.theme.obtainStyledAttributes(
            attrs,
            R.styleable.HyperColorizedBar,
            0, 0
        ).apply {
            try {
                val title = getString(R.styleable.HyperColorizedBar_titleText)
                titleView.text = title

                val iconResId = getResourceId(R.styleable.HyperColorizedBar_iconDrawables, 0)
                if (iconResId != 0) {
                    val icons = resources.obtainTypedArray(iconResId)
                    setIcons(icons)
                    icons.recycle()
                }
            } finally {
                recycle()
            }
        }
    }

    private fun setIcons(icons: TypedArray) {
        iconContainer.removeAllViews()
        for (i in 0 until icons.length()) {
            val icon = icons.getDrawable(i) ?: continue
            val imageView = ImageView(context).apply {
                setImageDrawable(icon)
                layoutParams = LinearLayout.LayoutParams(
                    LayoutParams.WRAP_CONTENT,
                    LayoutParams.WRAP_CONTENT
                ).apply {
                    topMargin = 10
                    rightMargin = if (i == icons.length() - 1) 20 else 80
                }

                setOnClickListener {
                    onIconClick?.invoke(i)
                }
            }
            iconContainer.addView(imageView)
        }
    }

    private fun animateBackgroundColor(toColor: Int) {
        val fromColor = currentBackgroundColor
        val colorAnimation = ValueAnimator.ofObject(ArgbEvaluator(), fromColor, toColor)
        colorAnimation.duration = 10
        colorAnimation.addUpdateListener { animator ->
            val color = animator.animatedValue as Int
            setBackgroundColor(color)
            currentBackgroundColor = color
            activity?.window?.statusBarColor = color
        }
        colorAnimation.start()
    }

    fun setupWithRecyclerView(recyclerView: RecyclerView) {
        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()

                if (firstVisibleItemPosition == 0 && dy < 0) {
                    animateBackgroundColor(
                        ContextCompat.getColor(
                            context,
                            R.color.background_color
                        )
                    )
                } else if (dy > 0) {
                    animateBackgroundColor(ContextCompat.getColor(context, R.color.main_color))
                }
            }
        })
    }
}