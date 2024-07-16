package com.snstudio.hyper.service

import com.snstudio.hyper.data.model.Media

interface JobCompletedCallback {
    fun onJobStart(id: String)
    fun onJobProgress(progress: Int)
    fun onJobCompleted(media: Media)

}