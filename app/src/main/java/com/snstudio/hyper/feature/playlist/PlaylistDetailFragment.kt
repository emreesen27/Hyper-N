package com.snstudio.hyper.feature.playlist

import android.annotation.SuppressLint
import android.os.Build
import android.view.ContextThemeWrapper
import android.view.View
import android.widget.PopupMenu
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
        MediaItemAdapter(onItemCLick = { _, pos ->
            setPlayList(pos)
        }, onMenuClick = { media, view -> showPopupMenu(media, view) })
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

    @SuppressLint("SetTextI18n")
    override fun observeData() =
        with(viewModel) {
            observe(playlistWithMediaLiveData) { mediaList ->
                mediaItemAdapter.setItems(mediaList.toMutableList())
                /*
                binding.listInfo.text = context?.getString(
                    R.string.media_list_info,
                    mediaList.size,
                    mediaList.sumOf { it.duration ?: 0 }.toDuration()
                )*/
            }
            observe(deleteMediaLiveData) { deletedItem ->
                mediaItemAdapter.removeItem(deletedItem)
            }
        }

    private fun initMediaViewModel() {
        mediaViewModel = ViewModelProvider(requireActivity())[MediaViewModel::class.java]
    }

    private fun initClickListener() {
        with(binding) {
            noMusicItem.click {
                showMediaPickerDialog()
            }
            playAll.click {
                setPlayList(0)
            }
            shuffle.click {
                setPlayList(0, true)
            }
            colorizedBar.setOnIconClickListener { index ->
                when (index) {
                    0 -> showMediaPickerDialog()
                    else -> return@setOnIconClickListener
                }
            }
        }
    }

    private fun getMediaForPlaylistOrdered() {
        viewModel.getMediaForPlaylistOrdered(args.playListId)
    }

    private fun setPlayList(
        pos: Int,
        shuffle: Boolean = false,
    ) {
        val mediaItems =
            mediaItemAdapter.mediaItems
                .let { items ->
                    if (shuffle) items.shuffled() else items
                }
                .map { media ->
                    MediaItemBuilder()
                        .setMediaId(media.localPath.orEmpty())
                        .setArtWorkBitmap(media.bitmap)
                        .setMediaTitle(media.title)
                        .build()
                }

        mediaViewModel.showPLayerMenu(true)
        mediaViewModel.setPlaylist(mediaItems, if (shuffle) 0 else pos)
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
                onMoveCallback = { from, to -> moveItem(from, to) },
                onMovedCallback = { movedItem() },
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

    private fun removeItem(media: Media) {
        viewModel.deleteMediaFromPlaylist(args.playListId, media)
    }

    private fun showPopupMenu(
        media: Media,
        view: View,
    ) {
        val popupMenu = PopupMenu(ContextThemeWrapper(context, R.style.PopupMenuTheme), view)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            popupMenu.setForceShowIcon(true)
        }
        popupMenu.menuInflater.inflate(R.menu.playlist_detail_menu, popupMenu.menu)
        popupMenu.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.remove -> {
                    removeItem(media)
                    true
                }

                else -> false
            }
        }
        popupMenu.show()
    }
}
