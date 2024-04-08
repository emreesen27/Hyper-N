package com.snstudio.hyper.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.snstudio.hyper.BR
import com.snstudio.hyper.R
import com.snstudio.hyper.data.Media
import com.snstudio.hyper.data.OperationType
import com.snstudio.hyper.databinding.ItemMediaBinding
import com.snstudio.hyper.databinding.ItemMediaSearchBinding

class MediaItemAdapter(
    private val onClick: ((Media, OperationType) -> Unit)? = null,
) : RecyclerView.Adapter<MediaItemAdapter.AutoCompleteViewHolder>() {
    private var mediaItems: List<Media> = emptyList()
    private var lastSelectedPosition = RecyclerView.NO_POSITION

    fun setItems(newItems: List<Media>) {
        val diffResult = DiffUtil.calculateDiff(MediaDiffCallback(mediaItems, newItems))
        mediaItems = newItems
        diffResult.dispatchUpdatesTo(this)
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

        }

        private fun bindMedia(media: Media) {
            println(media)
        }

        private fun bindMediaSearchType(media: Media) {
            val isSelected = absoluteAdapterPosition == adapter.lastSelectedPosition
            val itemBinding = binding as ItemMediaSearchBinding
            val itemClick: (OperationType) -> Unit = { operationType ->
                adapter.lastSelectedPosition = RecyclerView.NO_POSITION
                toggleMenu(true)
                adapter.onClick?.invoke(media, operationType)
            }

            itemBinding.root.setOnClickListener {
                if (adapter.lastSelectedPosition != absoluteAdapterPosition) {
                    adapter.notifyItemChanged(adapter.lastSelectedPosition)
                    adapter.lastSelectedPosition = absoluteAdapterPosition
                }

                toggleMenu(!isSelected)
                adapter.notifyItemChanged(absoluteAdapterPosition)
                adapter.lastSelectedPosition =
                    if (isSelected) RecyclerView.NO_POSITION else absoluteAdapterPosition
            }

            itemBinding.menu.visibility = if (isSelected) View.VISIBLE else View.GONE

            itemBinding.play.setOnClickListener {
                itemClick(OperationType.PLAY)
            }
            itemBinding.download.setOnClickListener {
                itemClick(OperationType.DOWNLOAD)
            }
            binding.info.setOnClickListener {
                itemClick(OperationType.INFO)
            }

        }

        private fun toggleMenu(isSelected: Boolean) {
            (binding as ItemMediaSearchBinding).menu.apply {
                isVisible = !isSelected
            }
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