package com.snstudio.hyper.feature.playlist

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.snstudio.hyper.core.extension.click
import com.snstudio.hyper.data.model.Playlist
import com.snstudio.hyper.databinding.ItemPlayListBinding

class PlaylistAdapter(private val onClick: ((Playlist) -> Unit)? = null) :
    RecyclerView.Adapter<PlaylistAdapter.PlaylistViewHolder>() {
    private var items: List<Playlist> = emptyList()

    fun setItems(newItems: List<Playlist>) {
        val diffResult = DiffUtil.calculateDiff(PlaylistDiffCallback(items, newItems))
        items = newItems
        diffResult.dispatchUpdatesTo(this)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaylistViewHolder =
        PlaylistViewHolder(
            ItemPlayListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )

    override fun onBindViewHolder(holder: PlaylistViewHolder, position: Int) {
        holder.bind(items[position])
        holder.itemView.click {
            onClick?.invoke(items[position])
        }
    }

    override fun getItemCount(): Int = items.size

    inner class PlaylistViewHolder(val binding: ItemPlayListBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: Playlist) {
            binding.playlistName.text = item.name
            binding.thumbnail.text = item.name.first().uppercase()
        }
    }

    inner class PlaylistDiffCallback(
        private val oldList: List<Playlist>,
        private val newList: List<Playlist>,
    ) : DiffUtil.Callback() {
        override fun getOldListSize(): Int = oldList.size

        override fun getNewListSize(): Int = newList.size

        override fun areItemsTheSame(
            oldItemPosition: Int,
            newItemPosition: Int,
        ): Boolean {
            return oldList[oldItemPosition].playlistId == newList[newItemPosition].playlistId
        }

        override fun areContentsTheSame(
            oldItemPosition: Int,
            newItemPosition: Int,
        ): Boolean {
            return oldList[oldItemPosition] == newList[newItemPosition]
        }
    }

}