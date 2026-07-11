package com.example.maintenancenotifications.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build

object NotificationChannelHelper {

    const val CHANNEL_ID = "maintenance_channel"

    fun create(context: Context) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            val channel = NotificationChannel(
                CHANNEL_ID,
                "Notificaciones de mantenimiento",
                NotificationManager.IMPORTANCE_HIGH
            )

            channel.description = "Canal para solicitudes de mantenimiento"

            val manager =
                context.getSystemService(NotificationManager::class.java)

            manager.createNotificationChannel(channel)
        }
    }
}