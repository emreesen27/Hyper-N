package com.snstudio.hyper.feature

import com.snstudio.hyper.core.base.BaseDialog
import com.snstudio.hyper.core.extension.click
import com.snstudio.hyper.databinding.DialogCreatePlayListBinding

class CreatePlaylistDialog(
) : BaseDialog<DialogCreatePlayListBinding>() {
    var onClick: ((String) -> Unit)? = null
    override val dialogTag: String
        get() = "CONFIRMATION_DIALOG"

    override fun getViewBinding() = DialogCreatePlayListBinding.inflate(layoutInflater)

    override var setCancelable: Boolean = false

    override fun setupViews() {
        with(binding) {
            close.click { dismiss() }
            btnSave.click { onClick?.invoke(playlistName.text.toString()) }
        }
    }
}