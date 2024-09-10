package com.snstudio.hyper.feature.library

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.snstudio.hyper.data.local.repository.MediaRepository
import com.snstudio.hyper.data.model.Media
import com.snstudio.hyper.util.PrefsTag
import com.snstudio.hyper.util.SharedPreferenceManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LibraryViewModel
    @Inject
    constructor(
        private val localMediaRepository: MediaRepository,
        private val sharedPreferenceManager: SharedPreferenceManager,
    ) : ViewModel() {
        val localMediaLiveData: LiveData<List<Media>> =
            localMediaRepository.localMediaList.asLiveData()

        fun deleteMedia(media: Media) =
            viewModelScope.launch {
                localMediaRepository.delete(media)
            }

        fun setTrueInfoDialogStatus() {
            sharedPreferenceManager.putBoolean(PrefsTag.DELETE_INFO_DIALOG_SHOW, true)
        }

        fun getInfoDialogStatus(): Boolean = sharedPreferenceManager.getBoolean(PrefsTag.DELETE_INFO_DIALOG_SHOW)
    }
