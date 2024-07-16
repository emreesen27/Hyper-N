package com.snstudio.hyper.adapter

import androidx.recyclerview.widget.DiffUtil
import com.snstudio.hyper.data.model.Media
import com.snstudio.hyper.data.model.Playlist


class PlaylistDiffCallback(
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