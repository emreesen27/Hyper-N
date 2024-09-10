package com.snstudio.hyper.feature.picker

import androidx.databinding.ObservableBoolean
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.snstudio.hyper.data.local.repository.MediaRepository
import com.snstudio.hyper.data.model.Media
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MediaPickerViewModel
    @Inject
    constructor(
        mediaRepository: MediaRepository,
    ) : ViewModel() {
        val localMediaLiveData: LiveData<List<Media>> = mediaRepository.localMediaList.asLiveData()

        val containsItemIsEmptyObservable = ObservableBoolean(false)
    }
