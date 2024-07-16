package com.snstudio.hyper.core.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavDirections
import androidx.navigation.fragment.findNavController
import androidx.viewbinding.ViewBinding

abstract class BaseFragment<VBinding : ViewBinding, VModel : ViewModel> : Fragment() {
    open var useSharedViewModel: Boolean = false

    protected lateinit var viewModel: VModel

    protected abstract fun getViewModelClass(): Class<VModel>

    protected lateinit var binding: VBinding

    protected abstract fun getViewBinding(): VBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        init()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        return binding.root
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)
        setupViews()
        observeData()
    }

    open fun setupViews() {}

    open fun observeData() {}

    fun navigate(directions: NavDirections) {
        findNavController().navigate(directions)
    }

    fun invalidateOptionsMenu() {
        requireActivity().invalidateOptionsMenu()
    }

    private fun init() {
        binding = getViewBinding()
        viewModel =
            if (useSharedViewModel) {
                ViewModelProvider(requireActivity())[getViewModelClass()]
            } else {
                ViewModelProvider(this)[getViewModelClass()]
            }
    }
}