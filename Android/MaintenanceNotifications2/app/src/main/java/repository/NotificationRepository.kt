package com.example.maintenancenotifications.repository

import androidx.compose.runtime.mutableStateListOf
import com.example.maintenancenotifications.model.NotificationItem

object NotificationRepository {

    val notifications = mutableStateListOf<NotificationItem>()

    fun add(notification: NotificationItem) {
        notifications.add(0, notification)
    }

    fun getAll(): List<NotificationItem> {
        return notifications
    }
}