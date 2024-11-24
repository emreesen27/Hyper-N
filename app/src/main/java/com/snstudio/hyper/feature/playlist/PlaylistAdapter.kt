package com.snstudio.hyper.feature.playlist

import android.content.Context
import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.View
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
    private val onItemCLick: ((Playlist) -> Unit)? = null,
    private val onMenuClick: ((Playlist, View) -> Unit)? = null,
) : RecyclerView.Adapter<PlaylistAdapter.PlaylistViewHolder>() {
    private var items: MutableList<Playlist> = mutableListOf()

    private val colorList =
        listOf(
            R.color.main_color,
            R.color.old_main_color_mid,
            R.color.old_main_color_light,
            R.color.purple_500,
            R.color.purple_700,
        )

    fun setItems(newItems: List<Playlist>) {
        val diffResult = DiffUtil.calculateDiff(PlaylistDiffCallback(items, newItems))
        items = newItems.toMutableList()
        diffResult.dispatchUpdatesTo(this)
    }

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
            onItemCLick?.invoke(items[position])
        }
    }

    override fun getItemCount(): Int = items.size

    inner class PlaylistViewHolder(val binding: ItemPlayListBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Playlist) {
            val randomColorResId = colorList.random()
            val color = ContextCompat.getColor(context, randomColorResId)
            with(binding) {
                thumbnail.backgroundTintList = ColorStateList.valueOf(color)
                thumbnail.text = item.name.first().toString()
                playlistName.text = item.name
                description.text = item.creationDate.formatAsDate()
                menu.click {
                    onMenuClick?.invoke(item, it)
                }
            }
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
