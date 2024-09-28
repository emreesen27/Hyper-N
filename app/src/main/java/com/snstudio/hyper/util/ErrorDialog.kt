package com.snstudio.hyper.util

import androidx.annotation.StringRes
import com.snstudio.hyper.core.base.BaseDialog
import com.snstudio.hyper.core.extension.click
import com.snstudio.hyper.databinding.DialogErrorBinding

class ErrorDialog(
    @StringRes private val errMessage: Int,
) : BaseDialog<DialogErrorBinding>() {
    override var setCancelable: Boolean = true

    override fun getViewBinding() = DialogErrorBinding.inflate(layoutInflater)

    override val dialogTag: String
        get() = "ERROR_DIALOG"

    override fun setupViews() {
        binding.title.setText(errMessage)
        binding.close.click { dismiss() }
    }
}
