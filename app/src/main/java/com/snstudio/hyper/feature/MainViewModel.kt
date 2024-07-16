package com.snstudio.hyper.feature

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.snstudio.hyper.data.model.Media
import com.snstudio.hyper.data.local.repository.MediaRepository
import com.snstudio.hyper.util.PathProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val pathProvider: PathProvider,
    private val localMediaRepository: MediaRepository
) : ViewModel() {

    private val _localMediaListLiveData = MutableLiveData<List<Media>>()
    val localMediaListLiveData: LiveData<List<Media>> = _localMediaListLiveData

    init {
        viewModelScope.launch {
            localMediaRepository.localMediaList.collect {
                _localMediaListLiveData.value = it
            }
        }
    }

    fun mediaIsSaved(media: Media): Boolean =
        localMediaListLiveData.value?.any { media.id == it.id } ?: false

    fun createMusicFolder() {
        pathProvider.createMusicDir()
    }

    fun insertMedia(media: Media) = viewModelScope.launch {
        localMediaRepository.insert(media)
    }.invokeOnCompletion { err ->
        if (err == null) println("saved succes")
    }


}