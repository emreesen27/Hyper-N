package com.snstudio.hyper.util

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.snstudio.hyper.core.base.BaseDialog
import com.snstudio.hyper.core.extension.click
import com.snstudio.hyper.core.extension.loadGif
import com.snstudio.hyper.databinding.DialogInfoBinding

class InfoDialog(
    @StringRes private val titleResId: Int,
    @DrawableRes private val imageResId: Int,
) : BaseDialog<DialogInfoBinding>() {
    override var setCancelable: Boolean = true
    override val dialogTag: String
        get() = "INFO_DIALOG"

    override fun getViewBinding(): DialogInfoBinding = DialogInfoBinding.inflate(layoutInflater)

    override fun setupViews() {
        with(binding) {
            title.setText(titleResId)
            close.click { dismiss() }
            image.loadGif(imageResId)
        }
    }
}
