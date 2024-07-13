package com.snstudio.hyper.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.snstudio.hyper.core.extension.click
import com.snstudio.hyper.data.Media
import com.snstudio.hyper.databinding.FragmentDetailBottomSheetBinding

class DetailBottomSheet(
    private val onDelete: (() -> Unit)? = null,
    private val media: Media
) : BottomSheetDialogFragment() {
    private val binding: FragmentDetailBottomSheetBinding by lazy {
        FragmentDetailBottomSheetBinding.inflate(
            layoutInflater
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initClickListener()
        initTitle()
    }

    private fun initTitle() {
        binding.title.text = media.title
    }

    private fun initClickListener() {
        with(binding) {
            ivClose.click { dismiss() }
            binding.btnDelete.click {
                dismiss()
                onDelete?.invoke()
            }
        }
    }

    companion object {
        const val TAG = "DetailBottomSheet"
    }
}