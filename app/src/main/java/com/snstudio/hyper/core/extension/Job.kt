package com.snstudio.hyper.core.extension

import androidx.annotation.StringRes
import com.snstudio.hyper.core.base.BaseJob
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationCompat.CATEGORY_PROGRESS
import androidx.core.app.NotificationCompat.PRIORITY_HIGH
import com.snstudio.hyper.R

fun BaseJob.postNotification(
    @StringRes title: Int,
    progress: Int,
) {
    val notificationBuilder =
        NotificationCompat.Builder(service, "FileOperationChannel")
    val notification =
        notificationBuilder.setContentTitle("File Operation Service")
            .setContentText(service.getString(title))
            .setSmallIcon(R.drawable.launcher_icon)
            .setPriority(PRIORITY_HIGH)
            .setOngoing(true)
            .setOnlyAlertOnce(true)
            .setForegroundServiceBehavior(NotificationCompat.FOREGROUND_SERVICE_IMMEDIATE)
            .setCategory(CATEGORY_PROGRESS)
            .setProgress(100, progress, false)
            .build()
    service.notificationManager.notify(id, notification)
}