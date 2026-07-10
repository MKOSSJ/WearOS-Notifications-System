package com.example.maintenancenotifications.network

data class TokenRequest(
    val token: String,
    val platform: String = "android"
)