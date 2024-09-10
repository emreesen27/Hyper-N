package com.snstudio.hyper.core.extension

import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationCompat.CATEGORY_PROGRESS
import androidx.core.app.NotificationCompat.PRIORITY_HIGH
import com.snstudio.hyper.R
import com.snstudio.hyper.core.base.BaseJob

fun BaseJob.postNotification(
    title: String,
    progress: Int,
) {
    val notificationBuilder =
        NotificationCompat.Builder(service, service.getString(R.string.notification_id))
    val notification =
        notificationBuilder.setContentTitle(service.getString(R.string.notification_name))
            .setContentText(title)
            .setSmallIcon(R.mipmap.ic_launcher_main)
            .setPriority(PRIORITY_HIGH)
            .setOngoing(true)
            .setOnlyAlertOnce(true)
            .setForegroundServiceBehavior(NotificationCompat.FOREGROUND_SERVICE_IMMEDIATE)
            .setCategory(CATEGORY_PROGRESS)
            .setProgress(100, progress, false)
            .build()
    service.notificationManager.notify(id, notification)
}
