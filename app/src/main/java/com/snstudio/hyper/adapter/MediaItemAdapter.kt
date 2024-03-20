package com.snstudio.hyper.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.snstudio.hyper.BR
import com.snstudio.hyper.R
import com.snstudio.hyper.data.Media
import com.snstudio.hyper.databinding.ItemMediaBinding
import com.snstudio.hyper.databinding.ItemMediaSearchBinding

class MediaItemAdapter(
    private val onClick: ((Media) -> Unit)? = null,
) : RecyclerView.Adapter<MediaItemAdapter.AutoCompleteViewHolder>() {
    private var mediaItems: List<Media> = emptyList()

    fun setItems(newItems: List<Media>) {
        val diffResult = DiffUtil.calculateDiff(MediaDiffCallback(mediaItems, newItems))
        mediaItems = newItems
        diffResult.dispatchUpdatesTo(this)
    }

    fun removeItems(mediaToRemove: List<Media>) {
        for (fileToRemove in mediaToRemove) {
            val position = mediaItems.indexOf(fileToRemove)
            if (position != RecyclerView.NO_POSITION) {
                mediaItems = mediaItems.toMutableList().apply { removeAt(position) }
                notifyItemRemoved(position)
            }
        }
    }

    fun addItem(newItems: List<Media>) {
        val updatedList = mediaItems.toMutableList().apply { addAll(newItems) }
        val diffResult = DiffUtil.calculateDiff(MediaDiffCallback(mediaItems, updatedList))
        mediaItems = updatedList
        diffResult.dispatchUpdatesTo(this)
    }


    override fun getItemViewType(position: Int): Int {
        val item = mediaItems[position]
        return when (item.type) {
            0 -> R.layout.item_media
            1 -> R.layout.item_media_search
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


            binding.root.setOnClickListener {
                with(adapter) {
                    val position = adapterPosition
                    if (position != RecyclerView.NO_POSITION) {
                        onClick?.invoke(mediaItems[position])
                    }
                }
            }
        }

        private fun bindMedia(media: Media) {
            //(binding as ItemMediaBinding).thumbnail.loadWithGlide(media.thumbnails)
        }

        private fun bindMediaSearchType(media: Media) {

        }

        companion object {
            fun create(
                inflater: LayoutInflater?,
                parent: ViewGroup?,
                viewType: Int,
                adapter: MediaItemAdapter,
            ): AutoCompleteViewHolder {
                val binding =
                    DataBindingUtil.inflate<ViewDataBinding>(inflater!!, viewType, parent, false)
                return AutoCompleteViewHolder(
                    binding,
                    adapter,
                )
            }
        }
    }

}