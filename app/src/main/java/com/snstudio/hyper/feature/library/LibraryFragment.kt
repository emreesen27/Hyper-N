package com.snstudio.hyper.feature.library

import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ItemTouchHelper
import com.snstudio.hyper.shared.MediaItemAdapter
import com.snstudio.hyper.core.base.BaseFragment
import com.snstudio.hyper.core.extension.observe
import com.snstudio.hyper.data.model.Media
import com.snstudio.hyper.data.MediaItemBuilder
import com.snstudio.hyper.databinding.FragmentLibraryBinding
import com.snstudio.hyper.shared.MediaViewModel
import com.snstudio.hyper.util.ItemTouchHelperCallback
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LibraryFragment : BaseFragment<FragmentLibraryBinding, LibraryViewModel>() {

    private lateinit var mediaViewModel: MediaViewModel
    private var mediaItemAdapter: MediaItemAdapter? = null
    override fun getViewModelClass() = LibraryViewModel::class.java

    override fun getViewBinding() = FragmentLibraryBinding.inflate(layoutInflater)

    override fun setupViews() {
        initMediaRecycler()
        initMediaViewModel()
        with(binding) {
            attachToolbar(colorizedBar, recyclerMedia)
        }
    }

    override fun observeData() {
        observe(viewModel.localMediaLiveData) { mediaList ->
            mediaItemAdapter?.setItems(mediaList.toMutableList())
        }
    }

    private fun initMediaRecycler() {
        if (mediaItemAdapter == null) {
            mediaItemAdapter = MediaItemAdapter { media ->
                playMedia(media)
            }
            val callback = ItemTouchHelperCallback(
                requireContext(),
                onSwipedCallback = { pos -> deleteMedia(pos) },
            )
            val itemTouchHelper = ItemTouchHelper(callback)
            binding.recyclerMedia.adapter = mediaItemAdapter
            itemTouchHelper.attachToRecyclerView(binding.recyclerMedia)
        }
    }

    private fun deleteMedia(pos: Int) {
        mediaItemAdapter?.let { adapter ->
            viewModel.deleteMedia(adapter.mediaItems[pos])
        }
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