package com.snstudio.hyper.adapter

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.snstudio.hyper.R
import kotlin.math.abs

class SimpleItemTouchHelperCallback(
    private val context: Context,
    private val adapter: MediaItemAdapter
) : ItemTouchHelper.Callback() {

    private val deleteIcon: Drawable? = ContextCompat.getDrawable(context, R.drawable.ic_delete)

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
        adapter.moveItem(viewHolder.absoluteAdapterPosition, target.absoluteAdapterPosition)
        return true
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        val position = viewHolder.absoluteAdapterPosition
        val removedItem = adapter.mediaItems[position]
        adapter.removeItem(position)

        Snackbar.make(viewHolder.itemView, "Item deleted", Snackbar.LENGTH_LONG)
            .setAction("Undo") {
                adapter.restoreItem(removedItem, position)
            }.show()
    }

    override fun onChildDraw(
        c: Canvas,
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        dX: Float,
        dY: Float,
        actionState: Int,
        isCurrentlyActive: Boolean
    ) {
        if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
            val itemView = viewHolder.itemView
            val iconMargin = (itemView.height - deleteIcon!!.intrinsicHeight) / 2
            val iconTop = itemView.top + iconMargin
            val iconBottom = iconTop + deleteIcon.intrinsicHeight

            val threshold = 150 // İkonun görünmesi için gereken minimum kaydırma mesafesi

            if (dX == 0f) {
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                return
            }

            if (dX < 0) { // Sol tarafa kaydırılıyor

                if (abs(dX) > threshold) {
                    val iconLeft = itemView.right - iconMargin - deleteIcon!!.intrinsicWidth
                    val iconRight = itemView.right - iconMargin
                    deleteIcon!!.setBounds(iconLeft, iconTop, iconRight, iconBottom)


                    // Arka plan rengi
                    val background = ColorDrawable(Color.RED)
                    background.setBounds(
                        itemView.right + dX.toInt(),
                        itemView.top,
                        itemView.right,
                        itemView.bottom
                    )
                    background.draw(c)

                    deleteIcon.draw(c)
                }
            }
        }

        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
    }



    override fun isLongPressDragEnabled(): Boolean {
        return true
    }
}
