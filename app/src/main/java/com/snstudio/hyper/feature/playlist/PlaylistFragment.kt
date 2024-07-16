package com.snstudio.hyper.feature.playlist

import com.snstudio.hyper.adapter.PlaylistAdapter
import com.snstudio.hyper.core.base.BaseFragment
import com.snstudio.hyper.core.extension.click
import com.snstudio.hyper.core.extension.observe
import com.snstudio.hyper.data.model.Playlist
import com.snstudio.hyper.databinding.FragmentPlaylistBinding
import com.snstudio.hyper.feature.CreatePlaylistDialog
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PlaylistFragment : BaseFragment<FragmentPlaylistBinding, PlaylistViewModel>() {

    private val playlistAdapter by lazy { PlaylistAdapter(requireContext()) }

    override fun getViewModelClass() = PlaylistViewModel::class.java
    override fun getViewBinding() = FragmentPlaylistBinding.inflate(layoutInflater)

    override fun setupViews() {
        initAdapter()
        initListener()
    }

    override fun observeData() {
        observe(viewModel.playListLiveData) {
            playlistAdapter.setItems(it)
        }
    }

    private fun initAdapter() {
        binding.recyclerPlaylist.adapter = playlistAdapter
    }

    private fun initListener() {
        with(binding) {
            noItem.click {
                val dialog = CreatePlaylistDialog()
                dialog.onClick = { playlistName ->
                    viewModel.insertPlayList(Playlist(name = playlistName))
                    dialog.dismiss()
                }
                dialog.showDialog(childFragmentManager)
            }
        }
    }

}