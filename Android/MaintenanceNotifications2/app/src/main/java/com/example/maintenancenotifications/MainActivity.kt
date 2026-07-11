package com.example.maintenancenotifications

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.example.maintenancenotifications.network.RetrofitClient
import com.example.maintenancenotifications.network.TokenRequest
import com.example.maintenancenotifications.notification.NotificationChannelHelper
import com.example.maintenancenotifications.ui.HomeScreen
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        NotificationChannelHelper.create(this)

        pedirPermiso()

        FirebaseMessaging.getInstance().token
            .addOnCompleteListener { task ->

                if (task.isSuccessful) {

                    val token = task.result

                    Log.d("TOKEN", token)

                    lifecycleScope.launch {
                        try {
                            val response = RetrofitClient.api.enviarToken(
                                TokenRequest(
                                    token = token,
                                    platform = "android"
                                )
                            )

                            if (response.isSuccessful) {
                                Log.d("API", "Token enviado correctamente")
                            } else {
                                Log.e("API", "Error al enviar token: ${response.code()}")
                            }

                        } catch (e: Exception) {
                            Log.e("API", "Error de conexión: ${e.message}")
                        }
                    }

                } else {
                    Log.e("TOKEN", "No se pudo obtener el token")
                }
            }

        setContent {
            HomeScreen()
        }
    }

    private fun pedirPermiso() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                    100
                )
            }
        }
    }
}