package com.snstudio.hyper.feature.home

import com.snstudio.hyper.core.base.BaseDialog
import com.snstudio.hyper.core.extension.click
import com.snstudio.hyper.databinding.DialogVersionControlBinding

class VersionDialog : BaseDialog<DialogVersionControlBinding>() {
    var onClick: ((Boolean) -> Unit)? = null

    override var setCancelable: Boolean = false
    override val dialogTag: String
        get() = "VERSION_CONTROL_DIALOG"

    override fun getViewBinding(): DialogVersionControlBinding = DialogVersionControlBinding.inflate(layoutInflater)

    override fun setupViews() {
        with(binding) {
            download.click {
                onClick?.invoke(true)
            }
            cancel.click {
                onClick?.invoke(false)
            }
        }
    }
}
