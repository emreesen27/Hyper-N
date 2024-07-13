package com.snstudio.hyper.ui.library

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.snstudio.hyper.data.Media
import com.snstudio.hyper.data.local.MediaRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class LibraryViewModel @Inject constructor(private val localMediaRepository: MediaRepository) :
    ViewModel() {

    val localMediaLiveData: LiveData<List<Media>> =
        localMediaRepository.localMediaList.asLiveData()

}