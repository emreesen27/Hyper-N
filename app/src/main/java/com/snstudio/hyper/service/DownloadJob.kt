package com.snstudio.hyper.service

import android.os.Environment
import com.snstudio.hyper.R
import com.snstudio.hyper.data.Media
import com.snstudio.hyper.core.base.BaseJob
import com.snstudio.hyper.core.extension.postNotification
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

    override fun run() {
        val client = OkHttpClient()

        println(url)
        val request = Request.Builder()
            .url(url)
            .build()

        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) throw IOException("Unexpected code $response")

            val totalBytes = response.body!!.contentLength()
            var downloadedBytes = 0L

            val inputStream = response.body!!.byteStream()
            val file =
                File("${Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).absolutePath}/${media.title}.mp3")
            val outputStream = FileOutputStream(file)

            val buffer = ByteArray(DEFAULT_BUFFER_SIZE)
            var bytesRead: Int

            inputStream.use { input ->
                outputStream.use { output ->
                    while (input.read(buffer).also { bytesRead = it } != -1) {
                        output.write(buffer, 0, bytesRead)
                        downloadedBytes += bytesRead
                        val progress = ((downloadedBytes.toDouble() / totalBytes) * 100).toInt()
                        postNotification(R.string.app_name, progress)
                        callback.onJobProgress(progress)
                    }
                }
            }
        }
    }


    override fun onCompleted() {
        callback.onJobCompleted()
    }
}