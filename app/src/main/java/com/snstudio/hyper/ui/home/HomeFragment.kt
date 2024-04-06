package com.snstudio.hyper.ui.home

import com.snstudio.hyper.util.DATA_KEY
import com.snstudio.hyper.adapter.MediaItemAdapter
import com.snstudio.hyper.util.RECEIVED
import com.snstudio.hyper.core.base.BaseFragment
import com.snstudio.hyper.core.extension.observe
import com.snstudio.hyper.databinding.FragmentHomeBinding
import com.snstudio.hyper.core.extension.toMediaList
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment : BaseFragment<FragmentHomeBinding, HomeViewModel>() {
    private var mediaItemAdapter: MediaItemAdapter? = null

    override fun getViewModelClass() = HomeViewModel::class.java

    override fun getViewBinding() = FragmentHomeBinding.inflate(layoutInflater)

    override fun observeData() {
        with(viewModel) {
            observe(receivedData) { call ->
                if (call.method == RECEIVED.HIGHLIGHTS_RECEIVED.received) {
                    call.argument<List<HashMap<String, String>>>(DATA_KEY)?.let { data ->
                        mediaItemAdapter?.setItems(data.toMediaList(0))
                    }
                    viewModel.highlightsProgress.set(false)
                }
            }
        }
    }

    override fun setupViews() {
        binding.vm = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
        initAdapter()
        initMenuClick()
    }

    private fun initMenuClick() {
        binding.search.setOnClickListener {
            navigate(HomeFragmentDirections.goToSearch())
        }
    }

    private fun initAdapter() {
        if (mediaItemAdapter == null) {
            mediaItemAdapter = MediaItemAdapter()
        }
        binding.recyclerMedia.apply {
            adapter = mediaItemAdapter
            set3DItem(true)
            setAlpha(true)
            setInfinite(true)
        }
    }
}
