package com.snstudio.hyper.service

interface JobCompletedCallback {
    fun onJobProgress(progress: Int)
    fun onJobCompleted()

}