package com.snstudio.hyper.feature.playlist

import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.ItemTouchHelper
import com.snstudio.hyper.adapter.MediaItemAdapter
import com.snstudio.hyper.adapter.SimpleItemTouchHelperCallback
import com.snstudio.hyper.core.base.BaseFragment
import com.snstudio.hyper.core.extension.click
import com.snstudio.hyper.core.extension.observe
import com.snstudio.hyper.databinding.FragmentPlaylistDetailBinding
import com.snstudio.hyper.feature.picker.MediaPickerDialog
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PlaylistDetailFragment : BaseFragment<FragmentPlaylistDetailBinding, PlaylistViewModel>() {

    private val args: PlaylistDetailFragmentArgs by navArgs()
    private val mediaItemAdapter by lazy {
        MediaItemAdapter(onClick = {})
    }

    override fun getViewModelClass() = PlaylistViewModel::class.java
    override fun getViewBinding() = FragmentPlaylistDetailBinding.inflate(layoutInflater)
    override fun setupViews() {
        initMediaRecycler()
        initClickListener()
        viewModel.getPlaylistWithMedia(args.playListId)
        with(binding) {
            vm = viewModel
            lifecycleOwner = viewLifecycleOwner
        }
    }

    override fun observeData() {
        observe(viewModel.playlistWithMediaLiveData) { mediaList ->
            mediaItemAdapter.setItems(mediaList.mediaList.toMutableList())
        }
    }

    private fun initClickListener() {
        with(binding) {
            addMusic.click { showPathSelectionDialog() }
        }
    }

    private fun showPathSelectionDialog() {
        MediaPickerDialog(
            selectedCallback = { mediaList ->
                mediaList?.let { viewModel.insertMediaListToPlaylist(args.playListId, it) }
            },
            containsItem = viewModel.playlistWithMediaLiveData.value?.mediaList
        ).showDialog(childFragmentManager)
    }

    private fun initMediaRecycler() {
        val callback = SimpleItemTouchHelperCallback(requireContext(), mediaItemAdapter)
        val itemTouchHelper = ItemTouchHelper(callback)
        binding.recyclerMedia.adapter = mediaItemAdapter
        itemTouchHelper.attachToRecyclerView(binding.recyclerMedia)
    }

}