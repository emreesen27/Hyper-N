package com.snstudio.hyper.feature.playlist

import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.ItemTouchHelper
import com.snstudio.hyper.core.base.BaseFragment
import com.snstudio.hyper.core.extension.observe
import com.snstudio.hyper.data.MediaItemBuilder
import com.snstudio.hyper.data.model.Media
import com.snstudio.hyper.databinding.FragmentPlaylistDetailBinding
import com.snstudio.hyper.feature.picker.MediaPickerDialog
import com.snstudio.hyper.shared.MediaItemAdapter
import com.snstudio.hyper.shared.MediaViewModel
import com.snstudio.hyper.util.ItemTouchHelperCallback
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PlaylistDetailFragment : BaseFragment<FragmentPlaylistDetailBinding, PlaylistViewModel>() {

    private lateinit var mediaViewModel: MediaViewModel
    private val args: PlaylistDetailFragmentArgs by navArgs()
    private val mediaItemAdapter by lazy {
        MediaItemAdapter(onClick = {
            setPlayList(it)
        })
    }

    override fun getViewModelClass() = PlaylistViewModel::class.java

    override fun getViewBinding() = FragmentPlaylistDetailBinding.inflate(layoutInflater)

    override fun setupViews() {
        initMediaViewModel()
        initMediaRecycler()
        initClickListener()
        getMediaForPlaylistOrdered()
        with(binding) {
            vm = viewModel
            lifecycleOwner = viewLifecycleOwner
            attachToolbar(colorizedBar, recyclerMedia, args.playListName)
        }
    }

    override fun observeData() = with(viewModel) {
        observe(playlistWithMediaLiveData) { mediaList ->
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

    private fun initMediaViewModel() {
        mediaViewModel = ViewModelProvider(requireActivity())[MediaViewModel::class.java]
    }

    private fun initClickListener() {
        binding.colorizedBar.setOnIconClickListener { index ->
            when (index) {
                0 -> showMediaPickerDialog()
                else -> return@setOnIconClickListener
            }
        }
    }

    private fun getMediaForPlaylistOrdered() {
        viewModel.getMediaForPlaylistOrdered(args.playListId)
    }

    private fun setPlayList(media: Media) {
        mediaItemAdapter.getSubMediaItems(media).let { mediaList ->
            val mediaItems = mediaList.map { media ->
                MediaItemBuilder()
                    .setMediaId(media.localPath.orEmpty())
                    .setMediaTitle(media.title)
                    .build()
            }
            mediaViewModel.setPlaylist(mediaItems)
        }
    }

    private fun showMediaPickerDialog() {
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