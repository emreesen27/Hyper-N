package com.snstudio.hyper.feature.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import com.snstudio.hyper.BR
import com.snstudio.hyper.R
import com.snstudio.hyper.core.extension.click
import com.snstudio.hyper.core.extension.loadWithGlideWithRadius
import com.snstudio.hyper.data.model.Media
import com.snstudio.hyper.databinding.ItemAddMusicBinding
import com.snstudio.hyper.databinding.ItemHighlightsBinding

class LastDownloadsAdapter(
    private val onItemCLick: ((Media, Int) -> Unit)? = null,
    private val onAddCLick: (() -> Unit)? = null,
) : RecyclerView.Adapter<LastDownloadsAdapter.AutoCompleteViewHolder>() {
    private var mediaItems: MutableList<Media> = mutableListOf()

    fun addItems(newItems: List<Media>) {
        mediaItems.clear()
        val startPosition = mediaItems.size + 1
        mediaItems.addAll(newItems)
        notifyItemRangeInserted(startPosition, newItems.size)
    }

    override fun getItemViewType(position: Int): Int {
        return if (position == 0) {
            R.layout.item_add_music
        } else {
            R.layout.item_highlights
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
            this@LastDownloadsAdapter,
        )
    }

    override fun onBindViewHolder(
        holder: AutoCompleteViewHolder,
        position: Int,
    ) {
        if (position == 0) {
            holder.bindAddButton()
        } else {
            holder.bindItem(mediaItems[position - 1])
        }
    }

    override fun getItemCount(): Int {
        return mediaItems.size + 1
    }

    class AutoCompleteViewHolder(
        private val binding: ViewDataBinding,
        private val adapter: LastDownloadsAdapter,
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bindItem(media: Media) {
            binding.setVariable(BR.item, media)
            binding.executePendingBindings()

            when (binding) {
                is ItemHighlightsBinding -> bindHighlights(media)
                is ItemAddMusicBinding -> bindAddMusicBinding()
            }
        }

        fun bindAddButton() {
            when (binding) {
                is ItemAddMusicBinding -> bindAddMusicBinding()
            }
        }

        private fun bindHighlights(media: Media) {
            (binding as ItemHighlightsBinding).apply {
                root.click { adapter.onItemCLick?.invoke(media, absoluteAdapterPosition) }
                val thumb = media.thumbnailMax ?: media.thumbnail
                image.loadWithGlideWithRadius(thumb.orEmpty())
                binding.title.text = media.title
            }
        }

        private fun bindAddMusicBinding() {
            (binding as ItemAddMusicBinding).apply {
                itemView.click { adapter.onAddCLick?.invoke() }
            }
        }

        companion object {
            fun create(
                inflater: LayoutInflater,
                parent: ViewGroup?,
                viewType: Int,
                adapter: LastDownloadsAdapter,
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
