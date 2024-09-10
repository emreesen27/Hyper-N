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
    private val swipeAction: SwipeAction = SwipeAction.DELETE,
    private val onMoveCallback: ((Int, Int) -> Unit)? = null,
    private val onMovedCallback: (() -> Unit)? = null,
    private val onSwipedCallback: ((Int) -> Unit),
) : ItemTouchHelper.Callback() {
    private val deleteIcon: Drawable? = ContextCompat.getDrawable(context, R.drawable.ic_delete)
    private val downloadIcon: Drawable? = ContextCompat.getDrawable(context, R.drawable.ic_download)

    private val deleteBackground =
        ColorDrawable(ContextCompat.getColor(context, R.color.delete_red))
    private val downloadBackground =
        ColorDrawable(ContextCompat.getColor(context, R.color.main_color))

    private var currentActionState: Int = ItemTouchHelper.ACTION_STATE_IDLE

    private var fromPosition: Int = 0
    private var toPosition: Int = 0

    override fun isLongPressDragEnabled(): Boolean {
        return onMoveCallback != null
    }

    override fun clearView(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
    ) {
        super.clearView(recyclerView, viewHolder)
        if (currentActionState == ItemTouchHelper.ACTION_STATE_DRAG) {
            onMovedCallback?.invoke()
        }
    }

    override fun getMovementFlags(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
    ): Int {
        val dragFlags = ItemTouchHelper.UP or ItemTouchHelper.DOWN
        val swipeFlags = ItemTouchHelper.LEFT
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

    override fun onSwiped(
        viewHolder: RecyclerView.ViewHolder,
        direction: Int,
    ) {
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
        isCurrentlyActive: Boolean,
    ) {
        val itemView = viewHolder.itemView
        currentActionState = actionState
        if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
            handleSwipe(canvas, itemView, dX)
        }
        super.onChildDraw(canvas, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
    }

    private fun handleSwipe(
        canvas: Canvas,
        itemView: View,
        dX: Float,
    ) {
        val icon: Drawable?
        val background: ColorDrawable

        when (swipeAction) {
            SwipeAction.DELETE -> {
                icon = deleteIcon
                background = deleteBackground
            }

            SwipeAction.DOWNLOAD -> {
                icon = downloadIcon
                background = downloadBackground
            }
        }

        icon?.let {
            if (dX < 0 && abs(dX) > 150) {
                val iconMargin = (itemView.height - it.intrinsicHeight) / 2
                val iconTop = itemView.top + iconMargin
                val iconBottom = iconTop + it.intrinsicHeight
                val iconLeft = itemView.right - iconMargin - it.intrinsicWidth
                val iconRight = itemView.right - iconMargin

                background.setBounds(
                    itemView.right + dX.toInt(),
                    itemView.top,
                    itemView.right,
                    itemView.bottom,
                )
                background.draw(canvas)

                it.setBounds(iconLeft, iconTop, iconRight, iconBottom)
                it.draw(canvas)
            }
        }
    }
}
