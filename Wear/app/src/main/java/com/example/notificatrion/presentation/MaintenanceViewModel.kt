package com.example.notificatrion.presentation

import android.app.Application
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import com.example.notificatrion.data.MaintenanceRequest
import com.google.android.gms.wearable.DataClient
import com.google.android.gms.wearable.Wearable
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.net.HttpURLConnection
import java.net.URL

class MaintenanceViewModel(application: Application) : AndroidViewModel(application), DataClient.OnDataChangedListener {

    private val _requests = mutableStateOf<List<MaintenanceRequest>>(listOf(
        MaintenanceRequest(
            id = "1",
            title = "¡URGENTE! Servidor SITE",
            description = "Sobrecalentamiento en rack 4. Temperatura 45°C.",
            status = "Pending",
            isRead = false
        ),
        MaintenanceRequest(
            id = "2",
            title = "Impresora Admin",
            description = "Atasco de papel y falta de tóner cian.",
            status = "In Progress",
            isRead = true
        ),
        MaintenanceRequest(
            id = "3",
            title = "Luz Pasillo B",
            description = "Cambiar 3 lámparas fundidas.",
            status = "Resolved",
            isRead = true
        ),
        MaintenanceRequest(
            id = "4",
            title = "Aire Acondicionado",
            description = "Mantenimiento preventivo unidad 02.",
            status = "Pending",
            isRead = false
        )
    ))
    val requests: State<List<MaintenanceRequest>> = _requests

    init {
        Wearable.getDataClient(application).addListener(this)
        fetchAndRegisterFcmToken()
    }

    private fun fetchAndRegisterFcmToken() {
        try {
            FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val token = task.result
                    android.util.Log.d("FCM", "Token: $token")
                    registerDeviceToken(token)
                } else {
                    android.util.Log.w("FCM", "Fetching FCM registration token failed", task.exception)
                }
            }
        } catch (e: Exception) {
            android.util.Log.e("FCM", "Firebase not initialized. Make sure google-services.json is present.")
        }
    }

    private fun registerDeviceToken(token: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                // Usando 10.0.2.2 para acceder al localhost de la máquina host desde el emulador
                val url = URL("http://10.0.2.2:5268/api/device-token")
                val conn = url.openConnection() as HttpURLConnection
                conn.requestMethod = "POST"
                conn.setRequestProperty("Content-Type", "application/json")
                conn.doOutput = true
                
                val jsonInputString = "{\"token\": \"$token\"}"
                conn.outputStream.use { os ->
                    val input = jsonInputString.toByteArray(Charsets.UTF_8)
                    os.write(input, 0, input.size)
                }
                
                android.util.Log.d("FCM", "Token registration response: ${conn.responseCode}")
            } catch (e: Exception) {
                android.util.Log.e("FCM", "Error registering token", e)
            }
        }
    }

    fun markAsResolved(requestId: String) {
        updateStatus(requestId, "Resolved")
    }

    fun markAsRead(requestId: String) {
        val currentList = _requests.value.toMutableList()
        val index = currentList.indexOfFirst { it.id == requestId }
        if (index != -1) {
            currentList[index] = currentList[index].copy(isRead = true)
            _requests.value = currentList
        }
    }

    fun ignoreRequest(requestId: String) {
        _requests.value = _requests.value.filter { it.id != requestId }
    }

    private fun updateStatus(requestId: String, status: String) {
        val currentList = _requests.value.toMutableList()
        val index = currentList.indexOfFirst { it.id == requestId }
        if (index != -1) {
            currentList[index] = currentList[index].copy(status = status, isRead = true)
            _requests.value = currentList
        }
    }

    override fun onDataChanged(dataEvents: com.google.android.gms.wearable.DataEventBuffer) {
    }

    override fun onCleared() {
        super.onCleared()
        Wearable.getDataClient(getApplication<Application>()).removeListener(this)
    }
}
