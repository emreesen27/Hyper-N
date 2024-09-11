package com.snstudio.hyper.feature.picker

import androidx.constraintlayout.widget.ConstraintLayout
import com.snstudio.hyper.core.base.BaseDialog
import com.snstudio.hyper.core.extension.click
import com.snstudio.hyper.data.model.Media
import com.snstudio.hyper.databinding.DialogMusicPickerBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MediaPickerDialog(
    private val selectedCallback: ((List<Media>?) -> Unit)? = null,
    private val mediaItems: List<Media>? = null,
) : BaseDialog<DialogMusicPickerBinding>() {
    private val mediaPickerAdapter: MediaPickerAdapter by lazy { MediaPickerAdapter() }

    override fun getViewBinding() = DialogMusicPickerBinding.inflate(layoutInflater)

    override val dialogTag: String
        get() = "MEDIA_PICKER_DIALOG"

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(
            ConstraintLayout.LayoutParams.MATCH_PARENT,
            ConstraintLayout.LayoutParams.MATCH_PARENT,
        )
    }

    override fun setupViews() {
        initAdapter()
        initCLickListener()
    }

    private fun initCLickListener() {
        binding.btnSave.click {
            selectedCallback?.invoke(mediaPickerAdapter.getSelectedItems())
            dismiss()
        }
    }

    private fun initAdapter() {
        binding.recycler.adapter = mediaPickerAdapter
        mediaItems?.let { mediaPickerAdapter.setItems(it) }
    }
}
