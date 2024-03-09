package com.snstudio.hyper

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.snstudio.hyper.databinding.ItemMediaBinding


class MediaAdapter() : RecyclerView.Adapter<MediaAdapter.MediaViewHolder>() {

    private var mediaItems: MutableList<Media> = mutableListOf()

    fun setItems(newItems: MutableList<Media>) {
        val diffResult = DiffUtil.calculateDiff(MediaDiffCallback(mediaItems, newItems))
        mediaItems = newItems
        diffResult.dispatchUpdatesTo(this)
    }

    fun addItem(newItems: Media) {
        mediaItems.add(newItems)
        notifyItemInserted(mediaItems.size)
    }

    fun removeItems(filesToRemove: List<Media>) {
        for (fileToRemove in filesToRemove) {
            val position = mediaItems.indexOf(fileToRemove)
            if (position != RecyclerView.NO_POSITION) {
                mediaItems = mediaItems.toMutableList().apply { removeAt(position) }
                notifyItemRemoved(position)
            }
        }
    }
    /*
    fun finishSelectionAndReset() {
        for (selectedItem in selectedItems) {
            val position = fileItems.indexOf(selectedItem)
            if (position != RecyclerView.NO_POSITION) {
                selectedItem.isSelected = false
                notifyItemChanged(position)
            }
        }
        selectedItems.clear()
        isSelectionModeActive = false
        selectionCallback?.onEndSelection()
    }*/

    fun getItems(): MutableList<Media> = mediaItems

    /*
    fun getSelectedItems(): MutableList<FileModel> = selectedItems

    fun selectionIsActive(): Boolean = isSelectionModeActive*/

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): MediaAdapter.MediaViewHolder {
        val binding = ItemMediaBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MediaViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: MediaAdapter.MediaViewHolder,
        position: Int,
    ) {
        holder.bind(mediaItems[position])
    }

    override fun getItemCount(): Int {
        return mediaItems.size
    }

    inner class MediaViewHolder(private val binding: ItemMediaBinding) :
        RecyclerView.ViewHolder(binding.root) {
        init {
            binding.root.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {

                }
            }
        }

        fun bind(media: Media) {
            binding.title.text = media.title
        }

    }
}