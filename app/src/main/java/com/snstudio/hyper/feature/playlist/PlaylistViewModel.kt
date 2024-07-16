package com.snstudio.hyper.feature.playlist

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.snstudio.hyper.data.local.repository.PlaylistMediaCrossRepository
import com.snstudio.hyper.data.local.repository.PlaylistRepository
import com.snstudio.hyper.data.model.Playlist
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PlaylistViewModel @Inject constructor(
    private val playlistRepository: PlaylistRepository,
    private val playlistMediaCrossRepository: PlaylistMediaCrossRepository,
) : ViewModel() {

    private val _playListLiveData = MutableLiveData<List<Playlist>>()
    val playListLiveData: LiveData<List<Playlist>> = _playListLiveData

    init {
        viewModelScope.launch {
            playlistRepository.allPlaylists.collect {
                _playListLiveData.value = it
            }
        }
    }

    fun insertPlayList(playlist: Playlist) = viewModelScope.launch {
        playlistRepository.insert(playlist)
    }

    fun deletePlaylist(playlist: Playlist) = viewModelScope.launch {
        playlistRepository.delete(playlist)
    }


}