package com.snstudio.hyper.core.base

import android.os.Handler
import android.os.Looper
import com.snstudio.hyper.R
import com.snstudio.hyper.service.JobService
import com.snstudio.hyper.core.extension.postNotification
import java.io.IOException
import java.io.InterruptedIOException
import java.util.Random

abstract class BaseJob {
    val id = Random().nextInt()

    internal val handler = Handler(Looper.getMainLooper())
    internal lateinit var service: JobService
        private set

    fun runOn(service: JobService) {
        this.service = service
        try {
            postNotification(R.string.app_name,0)
            run()
            onCompleted()
        } catch (e: InterruptedIOException) {
            e.printStackTrace()
        } catch (e: Exception) {
            e.printStackTrace()
            //handler.post { service.errorToast(e.toString()) }
        } finally {
            service.notificationManager.cancel(id)
        }
    }

    @Throws(IOException::class)
    protected abstract fun run()

    protected abstract fun onCompleted()
}