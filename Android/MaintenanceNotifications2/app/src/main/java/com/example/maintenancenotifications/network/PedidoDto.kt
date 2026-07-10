package com.example.maintenancenotifications.network

import com.google.gson.annotations.SerializedName

data class PedidoDto(
    val id: Long,
    val cliente: String,
    val direccion: String,
    val descripcion: String,
    val estado: String,

    @SerializedName("created_at")
    val createdAt: String? = null
)