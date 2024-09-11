package com.snstudio.hyper.feature.playlist

import android.content.Context
import android.graphics.PorterDuff
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.snstudio.hyper.R
import com.snstudio.hyper.core.extension.click
import com.snstudio.hyper.core.extension.formatAsDate
import com.snstudio.hyper.data.model.Playlist
import com.snstudio.hyper.databinding.ItemPlayListBinding

class PlaylistAdapter(
    private val context: Context,
    private val onClick: ((Playlist) -> Unit)? = null,
) :
    RecyclerView.Adapter<PlaylistAdapter.PlaylistViewHolder>() {
    private var items: MutableList<Playlist> = mutableListOf()

    private val colorList =
        listOf(
            R.color.main_color,
            R.color.main_color_mid,
            R.color.main_color_light,
            R.color.purple_500,
            R.color.purple_700,
        )

    fun setItems(newItems: List<Playlist>) {
        val diffResult = DiffUtil.calculateDiff(PlaylistDiffCallback(items, newItems))
        items = newItems.toMutableList()
        diffResult.dispatchUpdatesTo(this)
    }

    fun getItemWithPos(pos: Int): Playlist = items[pos]

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): PlaylistViewHolder =
        PlaylistViewHolder(
            ItemPlayListBinding.inflate(LayoutInflater.from(parent.context), parent, false),
        )

    override fun onBindViewHolder(
        holder: PlaylistViewHolder,
        position: Int,
    ) {
        holder.bind(items[position])
        holder.itemView.click {
            onClick?.invoke(items[position])
        }
    }

    override fun getItemCount(): Int = items.size

    inner class PlaylistViewHolder(val binding: ItemPlayListBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Playlist) {
            val randomColorResId = colorList.random()
            val color = ContextCompat.getColor(context, randomColorResId)

            binding.playlistName.text = item.name
            binding.thumbnail.setColorFilter(color, PorterDuff.Mode.SRC_IN)
            binding.description.text = item.creationDate.formatAsDate()
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
