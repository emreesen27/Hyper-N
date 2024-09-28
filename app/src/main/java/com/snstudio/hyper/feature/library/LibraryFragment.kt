package com.snstudio.hyper.feature.library

import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ItemTouchHelper
import com.snstudio.hyper.R
import com.snstudio.hyper.core.base.BaseFragment
import com.snstudio.hyper.core.extension.click
import com.snstudio.hyper.core.extension.observe
import com.snstudio.hyper.data.MediaItemBuilder
import com.snstudio.hyper.data.model.Media
import com.snstudio.hyper.databinding.FragmentLibraryBinding
import com.snstudio.hyper.shared.MediaItemAdapter
import com.snstudio.hyper.shared.MediaViewModel
import com.snstudio.hyper.util.InfoDialog
import com.snstudio.hyper.util.ItemTouchHelperCallback
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LibraryFragment : BaseFragment<FragmentLibraryBinding, LibraryViewModel>() {
    private lateinit var mediaViewModel: MediaViewModel

    private val mediaItemAdapter: MediaItemAdapter by lazy {
        MediaItemAdapter(onClick = { media, _ ->
            playMedia(media)
        })
    }

    override fun getViewModelClass() = LibraryViewModel::class.java

    override fun getViewBinding() = FragmentLibraryBinding.inflate(layoutInflater)

    override fun setupViews() {
        initMediaRecycler()
        createTouchHelperCallback()
        initMediaViewModel()
        initClickListener()
        with(binding) {
            attachToolbar(colorizedBar, null)
            binding.vm = viewModel
            binding.lifecycleOwner = viewLifecycleOwner
        }
    }

    override fun observeData() {
        observe(viewModel.localMediaLiveData) { mediaList ->
            mediaItemAdapter.setItems(mediaList.toMutableList())
            if (mediaList.isNotEmpty()) showInfoDialog()
        }
    }

    private fun initMediaRecycler() {
        binding.recyclerMedia.adapter = mediaItemAdapter
    }

    private fun initClickListener() {
        binding.noMusicItem.click {
            navigate(LibraryFragmentDirections.goToSearch())
        }
    }

    private fun createTouchHelperCallback() {
        val callback =
            ItemTouchHelperCallback(
                requireContext(),
                onSwipedCallback = { pos -> deleteMedia(pos) },
            )
        val itemTouchHelper = ItemTouchHelper(callback)
        itemTouchHelper.attachToRecyclerView(binding.recyclerMedia)
    }

    private fun deleteMedia(pos: Int) {
        viewModel.deleteMedia(mediaItemAdapter.mediaItems[pos])
    }

    private fun playMedia(media: Media) {
        media.localPath?.let {
            val item =
                MediaItemBuilder().setMediaId(it)
                    .setMediaTitle(media.title)
                    .build()
            with(mediaViewModel) {
                playMediaItem(item)
            }
        }
    }

    private fun initMediaViewModel() {
        mediaViewModel = ViewModelProvider(requireActivity())[MediaViewModel::class.java]
    }

    private fun showInfoDialog() {
        if (!viewModel.getInfoDialogStatus()) {
            InfoDialog(
                titleResId = R.string.how_to_delete,
                imageResId = R.drawable.delete_info,
            ).showDialog(childFragmentManager)
            viewModel.setTrueInfoDialogStatus()
        }
    }
}
