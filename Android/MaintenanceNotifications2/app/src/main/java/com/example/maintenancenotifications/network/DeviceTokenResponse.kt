package com.example.maintenancenotifications.network

data class DeviceTokenResponse(
    val id: Long,
    val token: String,
    val platform: String,
    val isActive: Boolean? = null,
    val createdAt: String? = null
)