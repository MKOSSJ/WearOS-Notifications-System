package com.example.maintenancenotifications.firebase

import android.util.Log
import com.example.maintenancenotifications.model.NotificationItem
import com.example.maintenancenotifications.notification.NotificationHelper
import com.example.maintenancenotifications.repository.NotificationRepository
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MyFirebaseMessagingService : FirebaseMessagingService() {

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d("FCM", "Nuevo token: $token")
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)

        Log.d("FCM", "NOTIFICACIÓN RECIBIDA")
        Log.d("FCM", "Title: ${message.notification?.title}")
        Log.d("FCM", "Body: ${message.notification?.body}")
        Log.d("FCM", "Data: ${message.data}")

        val titulo = message.notification?.title ?: "Nueva Solicitud"
        val cuerpo = message.notification?.body ?: "Sin descripción"

        val fecha = SimpleDateFormat(
            "dd/MM/yyyy",
            Locale.getDefault()
        ).format(Date())

        NotificationRepository.add(
            NotificationItem(
                title = titulo,
                message = cuerpo,
                date = fecha
            )
        )

        NotificationHelper.showNotification(
            this,
            titulo,
            cuerpo
        )
    }
}