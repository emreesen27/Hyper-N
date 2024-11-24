package com.snstudio.hyper.feature.library

import android.os.Build
import android.view.ContextThemeWrapper
import android.view.View
import android.widget.PopupMenu
import androidx.lifecycle.ViewModelProvider
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
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LibraryFragment : BaseFragment<FragmentLibraryBinding, LibraryViewModel>() {
    private lateinit var mediaViewModel: MediaViewModel

    private val mediaItemAdapter: MediaItemAdapter by lazy {
        MediaItemAdapter(onItemCLick = { media, _ ->
            playMedia(media)
        }, onMenuClick = { media, view ->
            showPopupMenu(media, view)
        })
    }

    override fun getViewModelClass() = LibraryViewModel::class.java

    override fun getViewBinding() = FragmentLibraryBinding.inflate(layoutInflater)

    override fun setupViews() {
        initMediaRecycler()
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

    private fun deleteMedia(media: Media) {
        viewModel.deleteMedia(media)
    }

    private fun playMedia(media: Media) {
        media.localPath?.let {
            val item =
                MediaItemBuilder().setMediaId(it)
                    .setArtWorkBitmap(media.bitmap)
                    .setMediaTitle(media.title)
                    .build()
            with(mediaViewModel) {
                showPLayerMenu(true)
                playMediaItem(item)
            }
        }
    }

    private fun initMediaViewModel() {
        mediaViewModel = ViewModelProvider(requireActivity())[MediaViewModel::class.java]
    }

    private fun showPopupMenu(
        media: Media,
        view: View,
    ) {
        val popupMenu = PopupMenu(ContextThemeWrapper(context, R.style.PopupMenuTheme), view)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            popupMenu.setForceShowIcon(true)
        }
        popupMenu.menuInflater.inflate(R.menu.library_menu, popupMenu.menu)
        popupMenu.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.delete -> {
                    showInfoDialog(media)
                    true
                }

                R.id.play -> {
                    playMedia(media)
                    true
                }

                else -> false
            }
        }

        popupMenu.show()
    }

    private fun showInfoDialog(media: Media) {
        InfoDialog(titleResId = R.string.selected_item_deleted, onClick = {
            deleteMedia(media)
        }).showDialog(childFragmentManager)
    }
}
