package com.snstudio.hyper.core.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import androidx.viewbinding.ViewBinding
import com.snstudio.hyper.R

abstract class BaseDialog<VBinding : ViewBinding> : DialogFragment() {
    protected lateinit var binding: VBinding

    protected abstract fun getViewBinding(): VBinding

    open var setCancelable: Boolean = true

    protected abstract val dialogTag: String

    open fun setupViews() {}

    fun showDialog(fragmentManager: FragmentManager) {
        show(fragmentManager, dialogTag)
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.apply {
            setLayout(
                ConstraintLayout.LayoutParams.MATCH_PARENT,
                ConstraintLayout.LayoutParams.WRAP_CONTENT,
            )
            addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            navigationBarColor = ContextCompat.getColor(requireActivity(), R.color.background_color)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = getViewBinding()
        isCancelable = setCancelable
        setStyle(STYLE_NO_TITLE, R.style.DialogTheme_transparent)
        dialog?.window?.setBackgroundDrawableResource(R.drawable.bg_dialog_rounded)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        return binding.root
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)
        setupViews()
    }
}
