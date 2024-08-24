package com.snstudio.hyper.feature.playlist

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.snstudio.hyper.data.local.repository.PlaylistMediaCrossRepository
import com.snstudio.hyper.data.local.repository.PlaylistRepository
import com.snstudio.hyper.data.model.Media
import com.snstudio.hyper.data.model.Playlist
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

    private val _playlistWithMediaLiveData = MutableLiveData<List<Media>>()
    val playlistWithMediaLiveData: LiveData<List<Media>> = _playlistWithMediaLiveData

    private val _swapOrdersLiveData = MutableLiveData<Pair<String, String>>()
    val swapOrderLiveData: LiveData<Pair<String, String>> = _swapOrdersLiveData

    private val _deleteMediaLiveData = MutableLiveData<Int>()
    val deleteMediaLiveData: LiveData<Int> = _deleteMediaLiveData


    init {
        getPlayList()
    }

    private fun getPlayList() = viewModelScope.launch {
        playlistRepository.allPlaylists.collectLatest { playlist ->
            _playlistLiveData.value = playlist
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

    fun updateOrders(playlistId: Long, fromId: String, fromOrder: Int, toId: String, toOrder: Int) =
        viewModelScope.launch {
            playlistMediaCrossRepository.updateOrders(playlistId, fromId, fromOrder, toId, toOrder)
        }


    fun getMediaForPlaylistOrdered(playlistId: Long) = viewModelScope.launch {
        val mediaList = playlistMediaCrossRepository.getMediaForPlaylistOrdered(playlistId)
        _playlistWithMediaLiveData.postValue(mediaList)
    }

    fun deleteMediaFromPlaylist(playlistId: Long, mediaId: String, pos: Int) =
        viewModelScope.launch {
            playlistMediaCrossRepository.deleteMediaFromPlaylist(playlistId, mediaId)
        }.invokeOnCompletion { throwable ->
            if (throwable == null) _deleteMediaLiveData.postValue(pos)
        }

}