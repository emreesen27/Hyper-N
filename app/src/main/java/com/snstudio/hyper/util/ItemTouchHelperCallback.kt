package com.snstudio.hyper.util

import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView

class ItemTouchHelperCallback(
    private val onMoveCallback: ((Int, Int) -> Unit)? = null,
    private val onMovedCallback: (() -> Unit)? = null,
) : ItemTouchHelper.Callback() {
    private var fromPosition: Int = 0
    private var toPosition: Int = 0

    override fun isLongPressDragEnabled(): Boolean {
        return onMoveCallback != null
    }

    override fun onSwiped(
        viewHolder: RecyclerView.ViewHolder,
        direction: Int,
    ) {}

    override fun clearView(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
    ) {
        super.clearView(recyclerView, viewHolder)
        onMovedCallback?.invoke()
    }

    override fun getMovementFlags(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
    ): Int {
        val dragFlags = ItemTouchHelper.UP or ItemTouchHelper.DOWN
        val swipeFlags = ItemTouchHelper.ACTION_STATE_IDLE
        return makeMovementFlags(dragFlags, swipeFlags)
    }

    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder,
    ): Boolean {
        fromPosition = viewHolder.absoluteAdapterPosition
        toPosition = target.absoluteAdapterPosition
        onMoveCallback?.invoke(fromPosition, toPosition)
        return true
    }
}
