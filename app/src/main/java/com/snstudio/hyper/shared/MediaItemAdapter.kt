package com.snstudio.hyper.shared

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.snstudio.hyper.BR
import com.snstudio.hyper.R
import com.snstudio.hyper.core.extension.click
import com.snstudio.hyper.data.model.Media
import com.snstudio.hyper.databinding.ItemMediaBinding
import com.snstudio.hyper.databinding.ItemMediaSearchBinding
import com.snstudio.hyper.util.MediaItemType

class MediaItemAdapter(
    private val onClick: ((Media) -> Unit)? = null,
) : RecyclerView.Adapter<MediaItemAdapter.AutoCompleteViewHolder>() {
    var mediaItems: MutableList<Media> = mutableListOf()
        private set

    fun setItems(newItems: MutableList<Media>) {
        val diffResult = DiffUtil.calculateDiff(MediaListDiffCallback(mediaItems, newItems))
        mediaItems = newItems
        diffResult.dispatchUpdatesTo(this)
    }

    fun addItem(newItems: MutableList<Media>) {
        val updatedList = mediaItems.apply { addAll(newItems) }
        val diffResult = DiffUtil.calculateDiff(MediaListDiffCallback(mediaItems, newItems))
        mediaItems = updatedList
        diffResult.dispatchUpdatesTo(this)
    }

    fun getSubMediaItems(media: Media): MutableList<Media> {
        val index = mediaItems.indexOf(media)
        return mediaItems.subList(index, mediaItems.size)
    }

    fun moveItem(
        fromPosition: Int,
        toPosition: Int,
    ) {
        val item = mediaItems.removeAt(fromPosition)
        mediaItems.add(toPosition, item)
        notifyItemMoved(fromPosition, toPosition)
    }

    fun removeItem(position: Int) {
        mediaItems.removeAt(position)
        notifyItemRemoved(position)
    }

    fun restoreItem(
        item: Media,
        position: Int,
    ) { // Todo
        mediaItems.add(position, item)
        notifyItemInserted(position)
    }

    override fun getItemViewType(position: Int): Int {
        val item = mediaItems[position]
        return when (item.type) {
            MediaItemType.LOCAL.key -> R.layout.item_media
            MediaItemType.SEARCH.key -> R.layout.item_media_search
            else -> throw RuntimeException("invalid object")
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): AutoCompleteViewHolder {
        return AutoCompleteViewHolder.create(
            LayoutInflater.from(parent.context),
            parent,
            viewType,
            this@MediaItemAdapter,
        )
    }

    override fun onBindViewHolder(
        holder: AutoCompleteViewHolder,
        position: Int,
    ) {
        holder.bindItem(mediaItems[position])
    }

    override fun getItemCount(): Int {
        return mediaItems.size
    }

    class AutoCompleteViewHolder(
        private val binding: ViewDataBinding,
        private val adapter: MediaItemAdapter,
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bindItem(data: Media) {
            binding.setVariable(BR.item, data)
            binding.executePendingBindings()

            when (binding) {
                is ItemMediaBinding -> bindMedia(data)
                is ItemMediaSearchBinding -> bindMediaSearchType(data)
            }
        }

        private fun bindMedia(media: Media) {
            val itemBinding = binding as ItemMediaBinding
            itemBinding.root.click {
                adapter.onClick?.invoke(media)
            }
        }

        private fun bindMediaSearchType(media: Media) {
            val itemBinding = binding as ItemMediaSearchBinding
            itemBinding.root.click {
                adapter.onClick?.invoke(media)
            }
        }

        companion object {
            fun create(
                inflater: LayoutInflater,
                parent: ViewGroup?,
                viewType: Int,
                adapter: MediaItemAdapter,
            ): AutoCompleteViewHolder {
                val binding =
                    DataBindingUtil.inflate<ViewDataBinding>(inflater, viewType, parent, false)
                return AutoCompleteViewHolder(
                    binding,
                    adapter,
                )
            }
        }
    }
}
