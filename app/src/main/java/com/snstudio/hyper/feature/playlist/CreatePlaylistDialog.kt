package com.snstudio.hyper.feature.playlist

import com.snstudio.hyper.R
import com.snstudio.hyper.core.base.BaseDialog
import com.snstudio.hyper.core.extension.click
import com.snstudio.hyper.databinding.DialogCreatePlayListBinding

class CreatePlaylistDialog : BaseDialog<DialogCreatePlayListBinding>() {
    var onClick: ((String) -> Unit)? = null

    override val dialogTag: String
        get() = "CONFIRMATION_DIALOG"

    override fun getViewBinding() = DialogCreatePlayListBinding.inflate(layoutInflater)

    override var setCancelable: Boolean = false

    override fun setupViews() {
        with(binding) {
            close.click { dismiss() }
            btnSave.click { checkInput() }
        }
    }

    private fun checkInput() {
        binding.playlistNameLayout.apply {
            val inputText = binding.playlistName.text?.toString().orEmpty()

            when {
                inputText.isEmpty() -> {
                    error = getString(R.string.cannot_be_empty)
                    return
                }

                inputText.length < 3 -> {
                    error = getString(R.string.three_characters_required)
                    return
                }

                else -> {
                    onClick?.invoke(inputText.replaceFirstChar { it.uppercase() })
                }
            }
        }
    }
}
