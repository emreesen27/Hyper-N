package com.snstudio.hyper.service

import com.snstudio.hyper.core.base.BaseJob
import com.snstudio.hyper.core.extension.postNotification
import com.snstudio.hyper.data.model.Media
import com.snstudio.hyper.util.PathProvider
import okhttp3.OkHttpClient
import okhttp3.Request
import okio.IOException
import java.io.File
import java.io.FileOutputStream

class DownloadJob(
    private val media: Media,
    private val url: String,
    private val callback: JobCompletedCallback,
) : BaseJob() {
    private var filePath: String? = null

    override fun run() {
        val pathProvider = PathProvider(service.application)
        callback.onJobStart(media.id)
        val client = OkHttpClient()
        val request =
            Request.Builder()
                .url(url)
                .build()

        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) throw IOException("Unexpected code $response")

            val totalBytes = response.body!!.contentLength()
            var downloadedBytes = 0L

            val inputStream = response.body!!.byteStream()
            val file = File("${pathProvider.musicDirPath}/${media.title}.mp3")
            filePath = file.absolutePath
            val outputStream = FileOutputStream(file)

            val buffer = ByteArray(DEFAULT_BUFFER_SIZE)
            var bytesRead: Int

            inputStream.use { input ->
                outputStream.use { output ->
                    while (input.read(buffer).also { bytesRead = it } != -1) {
                        output.write(buffer, 0, bytesRead)
                        downloadedBytes += bytesRead
                        val progress = ((downloadedBytes.toDouble() / totalBytes) * 100).toInt()
                        postNotification(media.title, progress)
                        callback.onJobProgress(progress)
                    }
                }
            }
        }
    }

    override fun onCompleted() {
        val newMedia: Media = media.copy(localPath = filePath)
        callback.onJobCompleted(newMedia)
    }
}
