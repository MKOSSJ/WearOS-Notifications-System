package com.example.maintenancenotifications.notification


import android.app.NotificationManager
import android.content.Context

import androidx.core.app.NotificationCompat
import com.example.maintenancenotifications.R

object NotificationHelper {

     const val CHANNEL_ID = NotificationChannelHelper.CHANNEL_ID

    fun showNotification(
        context: Context,
        title: String,
        message: String
    ) {

        val manager =
            context.getSystemService(Context.NOTIFICATION_SERVICE)
                    as NotificationManager

        val notification =
            NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(title)
                .setContentText(message)
                .setAutoCancel(true)
                .build()

        manager.notify(
            System.currentTimeMillis().toInt(),
            notification
        )

    }

}