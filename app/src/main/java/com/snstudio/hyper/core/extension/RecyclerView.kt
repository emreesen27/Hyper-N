package com.snstudio.hyper.core.extension

import android.content.Context
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.snstudio.hyper.R

fun RecyclerView.addOnScrolledToEnd(onScrolledToEnd: () -> Unit) {

    this.addOnScrollListener(object : RecyclerView.OnScrollListener() {

        private val VISIBLE_THRESHOLD = 5

        private var loading = true
        private var previousTotal = 0

        override fun onScrollStateChanged(
            recyclerView: RecyclerView,
            newState: Int
        ) {

            with(layoutManager as LinearLayoutManager) {

                val visibleItemCount = childCount
                val totalItemCount = itemCount
                val firstVisibleItem = findFirstVisibleItemPosition()

                if (loading && totalItemCount > previousTotal) {

                    loading = false
                    previousTotal = totalItemCount
                }

                if (!loading && (totalItemCount - visibleItemCount) <= (firstVisibleItem + VISIBLE_THRESHOLD)) {

                    onScrolledToEnd()
                    loading = true
                }
            }
        }
    })
}

fun RecyclerView.addDivider(context: Context) {
    val dividerItemDecoration = DividerItemDecoration(context, LinearLayoutManager.VERTICAL)
    val divider = ContextCompat.getDrawable(context, R.drawable.divider)
    if (divider != null) {
        dividerItemDecoration.setDrawable(divider)
        this.addItemDecoration(dividerItemDecoration)
    }
}