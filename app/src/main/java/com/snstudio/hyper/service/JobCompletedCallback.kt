package com.snstudio.hyper.service

import com.snstudio.hyper.data.model.Media

interface JobCompletedCallback {
    /**
     * Called when the job starts.
     *
     * @param id The unique identifier of the job.
     */
    fun onJobStart(id: String)

    /**
     * Called to report the job's progress.
     *
     * @param progress The current progress of the job as a percentage (0-100).
     */
    fun onJobProgress(progress: Int)

    /**
     * Called when the job is completed.
     *
     * @param media The media object produced or processed by the job.
     */
    fun onJobCompleted(media: Media)
}
