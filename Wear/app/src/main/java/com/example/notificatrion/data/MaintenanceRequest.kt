package com.example.notificatrion.data

data class MaintenanceRequest(
    val id: String = "",
    val title: String = "",
    val description: String = "",
    val timestamp: Long = System.currentTimeMillis(),
    val status: String = "Pending",
    val isRead: Boolean = false
)
