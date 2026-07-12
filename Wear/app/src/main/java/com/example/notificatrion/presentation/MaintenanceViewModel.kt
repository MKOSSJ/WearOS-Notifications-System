package com.example.notificatrion.presentation

import android.app.Application
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import com.example.notificatrion.data.MaintenanceRequest
import com.google.android.gms.wearable.DataClient
import com.google.android.gms.wearable.DataEvent
import com.google.android.gms.wearable.DataMapItem
import com.google.android.gms.wearable.Wearable
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MaintenanceViewModel(application: Application) : AndroidViewModel(application), DataClient.OnDataChangedListener {

    private val _requests = mutableStateOf<List<MaintenanceRequest>>(listOf(
        MaintenanceRequest(
            id = "example_1",
            title = "Impresora Fallando",
            description = "Revisar impresora del área de administración. No jala el tóner.",
            status = "Pending",
            isRead = false
        )
    ))
    val requests: State<List<MaintenanceRequest>> = _requests

    private val messageClient = Wearable.getMessageClient(application)
    private val nodeClient = Wearable.getNodeClient(application)

    init {
        Wearable.getDataClient(application).addListener(this)
    }

    fun markAsResolved(requestId: String) {
        // Enviar mensaje al teléfono para que allá se haga el POST real a la DB/Firebase
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val nodes = com.google.android.gms.tasks.Tasks.await(nodeClient.connectedNodes)
                for (node in nodes) {
                    messageClient.sendMessage(node.id, "/resolve_request", requestId.toByteArray())
                }
                // Simular el POST localmente por si se requiere directo (opcional)
                sendPostRequest(requestId, "Resolved")
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        // Actualizar localmente la UI
        updateRequestStatus(requestId, "Resolved")
    }

    fun markAsRead(requestId: String) {
        val currentList = _requests.value.toMutableList()
        val index = currentList.indexOfFirst { it.id == requestId }
        if (index != -1) {
            currentList[index] = currentList[index].copy(isRead = true)
            _requests.value = currentList
        }
        // Avisar al teléfono que ya se leyó
        CoroutineScope(Dispatchers.IO).launch {
            val nodes = com.google.android.gms.tasks.Tasks.await(nodeClient.connectedNodes)
            for (node in nodes) {
                messageClient.sendMessage(node.id, "/mark_as_read", requestId.toByteArray())
            }
        }
    }

    fun ignoreRequest(requestId: String) {
        _requests.value = _requests.value.filter { it.id != requestId }
        // Avisar al teléfono
        CoroutineScope(Dispatchers.IO).launch {
            val nodes = com.google.android.gms.tasks.Tasks.await(nodeClient.connectedNodes)
            for (node in nodes) {
                messageClient.sendMessage(node.id, "/ignore_request", requestId.toByteArray())
            }
        }
    }

    private fun updateRequestStatus(requestId: String, status: String) {
        val currentList = _requests.value.toMutableList()
        val index = currentList.indexOfFirst { it.id == requestId }
        if (index != -1) {
            currentList[index] = currentList[index].copy(status = status)
            _requests.value = currentList
        }
    }

    private suspend fun sendPostRequest(requestId: String, status: String) {
        // Aquí iría un Retrofit o OkHttp real si el reloj tuviera internet directo
        // Por ahora simulamos el log del POST
        android.util.Log.d("API_POST", "Enviando POST: /api/requests/$requestId con status $status")
    }

    override fun onDataChanged(dataEvents: com.google.android.gms.wearable.DataEventBuffer) {
        val newRequests = _requests.value.toMutableList()
        for (event in dataEvents) {
            if (event.type == DataEvent.TYPE_CHANGED) {
                val path = event.dataItem.uri.path
                if (path == "/maintenance_request") {
                    val dataMap = DataMapItem.fromDataItem(event.dataItem).dataMap
                    val id = dataMap.getString("id") ?: ""
                    val title = dataMap.getString("title") ?: ""
                    val description = dataMap.getString("description") ?: ""
                    val status = dataMap.getString("status") ?: "Pending"
                    val isRead = dataMap.getBoolean("isRead", false)
                    
                    val existingIndex = newRequests.indexOfFirst { it.id == id }
                    val request = MaintenanceRequest(id, title, description, status = status, isRead = isRead)
                    
                    if (existingIndex != -1) {
                        newRequests[existingIndex] = request
                    } else {
                        newRequests.add(request)
                    }
                }
            }
        }
        _requests.value = newRequests
    }

    override fun onCleared() {
        super.onCleared()
        Wearable.getDataClient(getApplication<Application>()).removeListener(this)
    }
}
