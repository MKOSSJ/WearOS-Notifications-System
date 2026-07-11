package com.example.maintenancenotifications.network

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface ApiService {

    @POST("api/devicetokens")
    suspend fun enviarToken(
        @Body tokenRequest: TokenRequest
    ): Response<DeviceTokenResponse>

    @GET("api/Pedido")
    suspend fun obtenerPedidos(): Response<List<PedidoDto>>
}