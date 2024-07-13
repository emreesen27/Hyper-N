package com.snstudio.hyper.ui.library

import androidx.lifecycle.ViewModelProvider
import com.snstudio.hyper.adapter.MediaItemAdapter
import com.snstudio.hyper.core.base.BaseFragment
import com.snstudio.hyper.core.extension.observe
import com.snstudio.hyper.data.Media
import com.snstudio.hyper.data.MediaItemBuilder
import com.snstudio.hyper.databinding.FragmentLibraryBinding
import com.snstudio.hyper.shared.MediaViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LibraryFragment : BaseFragment<FragmentLibraryBinding, LibraryViewModel>() {

    private lateinit var mediaViewModel: MediaViewModel
    private var mediaItemAdapter: MediaItemAdapter? = null
    override fun getViewModelClass() = LibraryViewModel::class.java

    override fun getViewBinding() = FragmentLibraryBinding.inflate(layoutInflater)

    override fun setupViews() {
        initAdapter()
        initMediaViewModel()
    }

    override fun observeData() {
        observe(viewModel.localMediaLiveData) { mediaList ->
            mediaItemAdapter?.setItems(mediaList)
        }
    }

    private fun initAdapter() {
        if (mediaItemAdapter == null) {
            mediaItemAdapter = MediaItemAdapter { media ->
                playMedia(media)
            }
        }
        binding.recyclerMedia.adapter = mediaItemAdapter
    }

    private fun playMedia(media: Media) {
        media.localPath?.let {
            val item = MediaItemBuilder().setMediaId(it)
                .setMediaTitle(media.title)
                .build()
            with(mediaViewModel) {
                playMediaItem(item)
                setCurrentMedia(media)
            }
        }
    }

    private fun initMediaViewModel() {
        mediaViewModel = ViewModelProvider(requireActivity())[MediaViewModel::class.java]
    }

}