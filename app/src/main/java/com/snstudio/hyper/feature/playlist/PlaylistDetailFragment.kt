package com.snstudio.hyper.feature.playlist

import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.ItemTouchHelper
import com.snstudio.hyper.R
import com.snstudio.hyper.core.base.BaseFragment
import com.snstudio.hyper.core.extension.click
import com.snstudio.hyper.core.extension.infoToast
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
        createTouchHelperCallback()
        initClickListener()
        getMediaForPlaylistOrdered()
        with(binding) {
            vm = viewModel
            lifecycleOwner = viewLifecycleOwner
            attachToolbar(colorizedBar, null, args.playListName)
        }
    }

    override fun observeData() =
        with(viewModel) {
            observe(playlistWithMediaLiveData) { mediaList ->
                mediaItemAdapter.setItems(mediaList.toMutableList())
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
        binding.noMusicItem.click {
            showMediaPickerDialog()
        }
    }

    private fun getMediaForPlaylistOrdered() {
        viewModel.getMediaForPlaylistOrdered(args.playListId)
    }

    private fun setPlayList(media: Media) {
        mediaItemAdapter.getSubMediaItems(media).let { mediaList ->
            val mediaItems =
                mediaList.map { media ->
                    MediaItemBuilder()
                        .setMediaId(media.localPath.orEmpty())
                        .setMediaTitle(media.title)
                        .build()
                }
            mediaViewModel.setPlaylist(mediaItems)
        }
    }

    private fun showMediaPickerDialog() {
        val allMediaList = viewModel.localMediaListLiveData.value.orEmpty()
        val playlistMedia = viewModel.playlistWithMediaLiveData.value.orEmpty()
        val filteredList =
            allMediaList.filterNot { mediaItem ->
                playlistMedia.any { it.id == mediaItem.id }
            }

        if (allMediaList.isEmpty()) {
            context?.let { it.infoToast(it.getString(R.string.no_music_downloaded_yet)) }
            return
        }
        if (filteredList.isEmpty()) {
            context?.let { it.infoToast(it.getString(R.string.all_music_contains)) }
            return
        }

        MediaPickerDialog(
            mediaItems = filteredList,
            selectedCallback = { mediaList ->
                mediaList?.let { viewModel.insertMediaListToPlaylist(args.playListId, it) }
            },
        ).showDialog(childFragmentManager)
    }

    private fun initMediaRecycler() {
        binding.recyclerMedia.adapter = mediaItemAdapter
    }

    private fun createTouchHelperCallback() {
        val callback =
            ItemTouchHelperCallback(
                requireContext(),
                onMovedCallback = { movedItem() },
                onSwipedCallback = { pos -> removeItem(pos) },
                onMoveCallback = { from, to -> moveItem(from, to) },
            )
        val itemTouchHelper = ItemTouchHelper(callback)
        itemTouchHelper.attachToRecyclerView(binding.recyclerMedia)
    }

    private fun moveItem(
        from: Int,
        to: Int,
    ) {
        mediaItemAdapter.moveItem(from, to)
    }

    private fun movedItem() {
        viewModel.updateOrders(
            args.playListId,
            mediaItemAdapter.mediaItems,
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
