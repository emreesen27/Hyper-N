package com.snstudio.hyper.feature.playlist

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.snstudio.hyper.data.local.repository.PlaylistMediaCrossRepository
import com.snstudio.hyper.data.local.repository.PlaylistRepository
import com.snstudio.hyper.data.model.Media
import com.snstudio.hyper.data.model.Playlist
import com.snstudio.hyper.data.model.PlaylistWithMedia
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PlaylistViewModel @Inject constructor(
    private val playlistRepository: PlaylistRepository,
    private val playlistMediaCrossRepository: PlaylistMediaCrossRepository,
) : ViewModel() {

    private val _playlistLiveData = MutableLiveData<List<Playlist>>()
    val playlistLiveData: LiveData<List<Playlist>> = _playlistLiveData

    private val _playlistWithMediaLiveData = MutableLiveData<PlaylistWithMedia>()
    val playlistWithMediaLiveData: LiveData<PlaylistWithMedia> = _playlistWithMediaLiveData

    init {
        viewModelScope.launch {
            playlistRepository.allPlaylists.collect {
                _playlistLiveData.value = it
            }
        }
    }

    fun insertPlaylist(playlist: Playlist) = viewModelScope.launch {
        playlistRepository.insert(playlist)
    }

    fun deletePlaylist(playlist: Playlist) = viewModelScope.launch {
        playlistRepository.delete(playlist)
    }


    fun insertMediaListToPlaylist(playlistId: Long, mediaList: List<Media>) =
        viewModelScope.launch {
            playlistMediaCrossRepository.insertMediaListToPlaylist(
                playlistId,
                mediaList.map { it.id })
        }

    fun getPlaylistWithMedia(playlistId: Long) = viewModelScope.launch {
        playlistMediaCrossRepository.getPlaylistWithMedia(playlistId).collectLatest {
            _playlistWithMediaLiveData.value = it
        }
    }

}