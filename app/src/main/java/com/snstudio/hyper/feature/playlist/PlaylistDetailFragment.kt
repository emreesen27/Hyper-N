package com.snstudio.hyper.feature.playlist

import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.ItemTouchHelper
import com.snstudio.hyper.util.ItemTouchHelperCallback
import com.snstudio.hyper.shared.MediaItemAdapter
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
        viewModel.getMediaForPlaylistOrdered(args.playListId)
        with(binding) {
            vm = viewModel
            lifecycleOwner = viewLifecycleOwner
        }
    }

    override fun observeData() = with(viewModel) {
        observe(playlistWithMediaLiveData) { mediaList ->
            println("amcik${mediaList[0].id}")
            println("amcik${mediaList[1].id}")
            mediaItemAdapter.setItems(mediaList.toMutableList())
        }
        observe(swapOrderLiveData) {
            activity?.runOnUiThread {
                mediaItemAdapter.movedItem(it.first, it.second)
            }
        }
        observe(deleteMediaLiveData) { deletedItemPos ->
            mediaItemAdapter.removeItem(deletedItemPos)
        }
    }

    private fun initClickListener() {
        with(binding) {
            addMusic.click {
                showPathSelectionDialog()
                //viewModel.cacik(args.playListId, listOf())
            }
        }
    }

    private fun showPathSelectionDialog() {
        MediaPickerDialog(
            selectedCallback = { mediaList ->
                mediaList?.let { viewModel.insertMediaListToPlaylist(args.playListId, it) }
            },
            containsItem = viewModel.playlistWithMediaLiveData.value
        ).showDialog(childFragmentManager)
    }

    private fun initMediaRecycler() {
        val callback = ItemTouchHelperCallback(
            requireContext(),
            onMovedCallback = { from, to -> movedItem(from, to) },
            onSwipedCallback = { pos -> removeItem(pos) },
            onMoveCallback = { from, to -> moveItem(from, to) }
        )
        val itemTouchHelper = ItemTouchHelper(callback)
        binding.recyclerMedia.adapter = mediaItemAdapter
        itemTouchHelper.attachToRecyclerView(binding.recyclerMedia)
    }

    private fun moveItem(from: Int, to: Int) {
        mediaItemAdapter.moveItem(from, to)
    }

    private fun movedItem(from: Int, to: Int) {
        val fromId = mediaItemAdapter.mediaItems[from].id
        val toId = mediaItemAdapter.mediaItems[to].id

        viewModel.updateOrders(
            args.playListId,
            fromId, to,
            toId, from
        )
    }

    private fun removeItem(pos: Int) {
        viewModel.deleteMediaFromPlaylist(args.playListId, mediaItemAdapter.mediaItems[pos].id, pos)
        /* Todo
        Snackbar.make(viewHolder.itemView, "Item deleted", Snackbar.LENGTH_LONG)
            .setAction("Undo") {
                adapter.restoreItem(removedItem, position)
            }.show()*/
    }

}