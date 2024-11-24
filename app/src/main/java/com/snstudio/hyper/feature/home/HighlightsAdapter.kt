package com.snstudio.hyper.feature.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.snstudio.hyper.core.extension.click
import com.snstudio.hyper.core.extension.loadWithGlideWithRadius
import com.snstudio.hyper.data.model.Media
import com.snstudio.hyper.databinding.ItemHighlightsBinding

class HighlightsAdapter(
    private val onItemCLick: ((Media) -> Unit)? = null,
) : RecyclerView.Adapter<HighlightsAdapter.HighlightsViewHolder>() {
    private var items: MutableList<Media> = mutableListOf()

    fun setItems(newItems: List<Media>) {
        val diffResult = DiffUtil.calculateDiff(HighlightsDiffCallback(items, newItems))
        items = newItems.toMutableList()
        diffResult.dispatchUpdatesTo(this)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): HighlightsViewHolder =
        HighlightsViewHolder(
            ItemHighlightsBinding.inflate(LayoutInflater.from(parent.context), parent, false),
        )

    override fun onBindViewHolder(
        holder: HighlightsViewHolder,
        position: Int,
    ) {
        holder.bind(items[position])
        holder.itemView.click {
            onItemCLick?.invoke(items[position])
        }
    }

    override fun getItemCount(): Int = items.size

    inner class HighlightsViewHolder(val binding: ItemHighlightsBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Media) {
            with(binding) {
                val thumb = item.thumbnailMax ?: item.thumbnail
                image.loadWithGlideWithRadius(thumb.orEmpty())
                title.text = item.title
            }
        }
    }

    inner class HighlightsDiffCallback(
        private val oldList: List<Media>,
        private val newList: List<Media>,
    ) : DiffUtil.Callback() {
        override fun getOldListSize(): Int = oldList.size

        override fun getNewListSize(): Int = newList.size

        override fun areItemsTheSame(
            oldItemPosition: Int,
            newItemPosition: Int,
        ): Boolean {
            return oldList[oldItemPosition].id == newList[newItemPosition].id
        }

        override fun areContentsTheSame(
            oldItemPosition: Int,
            newItemPosition: Int,
        ): Boolean {
            return oldList[oldItemPosition] == newList[newItemPosition]
        }
    }
}
