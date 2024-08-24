package com.snstudio.hyper.shared

import androidx.recyclerview.widget.DiffUtil
import com.snstudio.hyper.data.model.Media

class MediaListDiffCallback(
    private val oldList: List<Media>,
    private val newList: List<Media>,
) : DiffUtil.Callback() {
    override fun getOldListSize(): Int = oldList.size

    override fun getNewListSize(): Int = newList.size

    override fun areItemsTheSame(
        oldItemPosition: Int,
        newItemPosition: Int,
    ): Boolean {
        return oldList[oldItemPosition].id == newList[newItemPosition].id
    }

    override fun areContentsTheSame(
        oldItemPosition: Int,
        newItemPosition: Int,
    ): Boolean {
        return oldList[oldItemPosition].id == newList[newItemPosition].id &&
                oldList[oldItemPosition].thumbnail == newList[newItemPosition].thumbnail &&
                oldList[oldItemPosition].duration == newList[newItemPosition].duration &&
                oldList[oldItemPosition].url == newList[newItemPosition].url
    }
}