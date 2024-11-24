package com.snstudio.hyper.feature.playlist

import android.os.Build
import android.view.ContextThemeWrapper
import android.view.View
import android.widget.PopupMenu
import com.snstudio.hyper.R
import com.snstudio.hyper.core.base.BaseFragment
import com.snstudio.hyper.core.extension.click
import com.snstudio.hyper.core.extension.observe
import com.snstudio.hyper.data.model.Playlist
import com.snstudio.hyper.databinding.FragmentPlaylistBinding
import com.snstudio.hyper.util.InfoDialog
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PlaylistFragment : BaseFragment<FragmentPlaylistBinding, PlaylistViewModel>() {
    private val playlistAdapter by lazy {
        PlaylistAdapter(requireContext(), onItemCLick = {
            navigatePlaylistDetail(it)
        }, onMenuClick = { playlist, view -> showPopupMenu(playlist, view) })
    }

    override var useSharedViewModel = true

    override fun getViewModelClass() = PlaylistViewModel::class.java

    override fun getViewBinding() = FragmentPlaylistBinding.inflate(layoutInflater)

    override fun setupViews() {
        initClickListener()
        initPlaylistRecycler()
        with(binding) {
            attachToolbar(colorizedBar, null)
            vm = viewModel
            lifecycleOwner = viewLifecycleOwner
        }
    }

    override fun observeData() {
        observe(viewModel.playlistLiveData) {
            playlistAdapter.setItems(it)
        }
    }

    private fun initClickListener() {
        with(binding) {
            noPlaylistItem.click {
                showCreatePlaylistDialog()
            }
            colorizedBar.setOnIconClickListener {
                showCreatePlaylistDialog()
            }
        }
    }

    private fun showCreatePlaylistDialog() {
        val dialog = CreatePlaylistDialog()
        dialog.onClick = { playlistName ->
            viewModel.insertPlaylist(
                Playlist(
                    name = playlistName,
                    creationDate = System.currentTimeMillis(),
                ),
            )
            dialog.dismiss()
        }
        dialog.showDialog(childFragmentManager)
    }

    private fun initPlaylistRecycler() {
        binding.recyclerPlaylist.adapter = playlistAdapter
    }

    private fun navigatePlaylistDetail(playlist: Playlist) {
        val action =
            PlaylistFragmentDirections.goToPlaylistDetail(playlist.playlistId, playlist.name)
        navigate(action)
    }

    private fun showPopupMenu(
        playlist: Playlist,
        view: View,
    ) {
        val popupMenu = PopupMenu(ContextThemeWrapper(context, R.style.PopupMenuTheme), view)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            popupMenu.setForceShowIcon(true)
        }
        popupMenu.menuInflater.inflate(R.menu.playlist_menu, popupMenu.menu)
        popupMenu.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.delete -> {
                    showInfoDialog(playlist)
                    true
                }

                else -> false
            }
        }

        popupMenu.show()
    }

    private fun deletePlaylist(playlist: Playlist) {
        viewModel.deletePlaylist(playlist)
    }

    private fun showInfoDialog(playlist: Playlist) {
        InfoDialog(titleResId = R.string.selected_item_deleted, onClick = {
            deletePlaylist(playlist)
        }).showDialog(childFragmentManager)
    }
}
