package com.snstudio.hyper.ui.library

import com.snstudio.hyper.core.base.BaseFragment
import com.snstudio.hyper.databinding.FragmentLibraryBinding

class LibraryFragment : BaseFragment<FragmentLibraryBinding, LibraryViewModel>() {

    override fun getViewModelClass() = LibraryViewModel::class.java

    override fun getViewBinding() = FragmentLibraryBinding.inflate(layoutInflater)

}