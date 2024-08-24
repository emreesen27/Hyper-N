package com.snstudio.hyper.util

import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.view.View
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.snstudio.hyper.R
import kotlin.math.abs

class ItemTouchHelperCallback(
    context: Context,
    private val onMoveCallback: ((Int, Int) -> Unit),
    private val onMovedCallback: ((Int, Int) -> Unit),
    private val onSwipedCallback: ((Int) -> Unit)
) : ItemTouchHelper.Callback() {

    private val deleteIcon: Drawable? = ContextCompat.getDrawable(context, R.drawable.ic_delete)
    private val deleteBackground =
        ColorDrawable(ContextCompat.getColor(context, R.color.delete_red))

    private var currentActionState: Int = ItemTouchHelper.ACTION_STATE_IDLE

    private var fromPosition: Int = 0
    private var toPosition: Int = 0

    override fun isLongPressDragEnabled(): Boolean {
        return true
    }

    override fun clearView(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder) {
        super.clearView(recyclerView, viewHolder)
        if (currentActionState == ItemTouchHelper.ACTION_STATE_DRAG) {
            onMovedCallback.invoke(fromPosition, toPosition)
        }
    }


    override fun getMovementFlags(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder
    ): Int {
        val dragFlags = ItemTouchHelper.UP or ItemTouchHelper.DOWN
        val swipeFlags = ItemTouchHelper.LEFT
        return makeMovementFlags(dragFlags, swipeFlags)
    }

    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {
        fromPosition = viewHolder.absoluteAdapterPosition
        toPosition = target.absoluteAdapterPosition
        onMoveCallback.invoke(fromPosition, toPosition)
        return true
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        val position = viewHolder.absoluteAdapterPosition
        onSwipedCallback.invoke(position)
    }

    override fun onChildDraw(
        canvas: Canvas,
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        dX: Float,
        dY: Float,
        actionState: Int,
        isCurrentlyActive: Boolean
    ) {
        val itemView = viewHolder.itemView
        currentActionState = actionState
        if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
            handleSwipe(canvas, itemView, dX)
        }
        super.onChildDraw(canvas, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
    }

    private fun handleSwipe(canvas: Canvas, itemView: View, dX: Float) {
        deleteIcon?.let { icon ->
            if (dX < 0 && abs(dX) > 150) {
                val iconMargin = (itemView.height - icon.intrinsicHeight) / 2
                val iconTop = itemView.top + iconMargin
                val iconBottom = iconTop + icon.intrinsicHeight
                val iconLeft = itemView.right - iconMargin - icon.intrinsicWidth
                val iconRight = itemView.right - iconMargin

                deleteBackground.setBounds(
                    itemView.right + dX.toInt(),
                    itemView.top,
                    itemView.right,
                    itemView.bottom
                )
                deleteBackground.draw(canvas)

                icon.setBounds(iconLeft, iconTop, iconRight, iconBottom)
                icon.draw(canvas)
            }
        }
    }
}
