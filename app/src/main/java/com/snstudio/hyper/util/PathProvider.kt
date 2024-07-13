package com.snstudio.hyper.util

import android.app.Application
import java.io.File
import javax.inject.Inject

class PathProvider @Inject constructor(application: Application) {
    private val externalFiles: File? = application.getExternalFilesDir(null)

    val musicDir: File
        get() = File(externalFiles, "HyperMusic")

    val musicDirPath: String
        get() = File(externalFiles, "HyperMusic").path

    fun createMusicDir(): Boolean {
        return musicDir.exists() || musicDir.mkdirs()
    }

}