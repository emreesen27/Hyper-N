package com.snstudio.hyper

import androidx.lifecycle.ViewModel
import com.snstudio.hyper.util.PathProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val pathProvider: PathProvider
) : ViewModel() {

    fun createMusicFolder() {
        pathProvider.createMusicDir()
    }

}