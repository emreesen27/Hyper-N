package com.snstudio.hyper.feature.playlist

import androidx.recyclerview.widget.ItemTouchHelper
import com.snstudio.hyper.core.base.BaseFragment
import com.snstudio.hyper.core.extension.click
import com.snstudio.hyper.core.extension.observe
import com.snstudio.hyper.data.model.Playlist
import com.snstudio.hyper.databinding.FragmentPlaylistBinding
import com.snstudio.hyper.util.ItemTouchHelperCallback
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PlaylistFragment : BaseFragment<FragmentPlaylistBinding, PlaylistViewModel>() {
    private val playlistAdapter by lazy {
        PlaylistAdapter(requireContext(), onClick = {
            navigatePlaylistDetail(it)
        })
    }

    override var useSharedViewModel = true

    override fun getViewModelClass() = PlaylistViewModel::class.java

    override fun getViewBinding() = FragmentPlaylistBinding.inflate(layoutInflater)

    override fun setupViews() {
        initClickListener()
        initPlaylistRecycler()
        createTouchHelperCallback()
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

    private fun createTouchHelperCallback() {
        val callback =
            ItemTouchHelperCallback(
                requireContext(),
                onSwipedCallback = { pos ->
                    viewModel.deletePlaylist(
                        playlistAdapter.getItemWithPos(
                            pos,
                        ),
                    )
                },
            )
        val itemTouchHelper = ItemTouchHelper(callback)
        itemTouchHelper.attachToRecyclerView(binding.recyclerPlaylist)
    }

    private fun navigatePlaylistDetail(playlist: Playlist) {
        val action =
            PlaylistFragmentDirections.goToPlaylistDetail(playlist.playlistId, playlist.name)
        navigate(action)
    }
}
