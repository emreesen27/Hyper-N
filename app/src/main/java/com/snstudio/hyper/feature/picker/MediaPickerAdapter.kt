package com.snstudio.hyper.feature.picker

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.snstudio.hyper.BR
import com.snstudio.hyper.data.model.Media
import com.snstudio.hyper.databinding.ItemMusicPickerBinding
import com.snstudio.hyper.shared.MediaListDiffCallback

class MediaPickerAdapter : RecyclerView.Adapter<MediaPickerAdapter.MediaViewHolder>() {

    private var mediaItems: List<Media> = emptyList()
    private val selectedItems: MutableList<Media> = mutableListOf()

    fun setItems(newItems: List<Media>) {
        val diffResult = DiffUtil.calculateDiff(
            MediaListDiffCallback(
                mediaItems,
                newItems
            )
        )
        mediaItems = newItems
        diffResult.dispatchUpdatesTo(this)
    }

    fun getSelectedItems(): List<Media> = selectedItems.toList()

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): MediaViewHolder {
        return MediaViewHolder(
            ItemMusicPickerBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(
        holder: MediaViewHolder,
        position: Int,
    ) {
        holder.bindItem(mediaItems[position])
    }

    override fun getItemCount(): Int {
        return mediaItems.size
    }

    inner class MediaViewHolder(
        private val binding: ItemMusicPickerBinding,
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bindItem(media: Media) = with(binding) {
            setVariable(BR.mediaItem, media)
            executePendingBindings()

            cbSelected.isChecked = selectedItems.any { it.id == media.id }
            cbSelected.setOnCheckedChangeListener { _, isChecked ->
                updateItemSelection(media, isChecked)
            }
        }

        private fun updateItemSelection(media: Media, isSelected: Boolean) {
            if (isSelected) {
                selectedItems.add(media)
            } else {
                selectedItems.remove(media)
            }
        }
    }
}
