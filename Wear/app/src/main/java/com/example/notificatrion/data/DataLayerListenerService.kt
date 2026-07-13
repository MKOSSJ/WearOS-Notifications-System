package com.example.notificatrion.data

import android.util.Log
import com.google.android.gms.wearable.DataEvent
import com.google.android.gms.wearable.DataEventBuffer
import com.google.android.gms.wearable.DataMapItem
import com.google.android.gms.wearable.WearableListenerService

class DataLayerListenerService : WearableListenerService() {

    override fun onDataChanged(dataEvents: DataEventBuffer) {
        super.onDataChanged(dataEvents)
        for (event in dataEvents) {
            if (event.type == DataEvent.TYPE_CHANGED) {
                val path = event.dataItem.uri.path
                if (path == "/maintenance_request") {
                    val dataMap = DataMapItem.fromDataItem(event.dataItem).dataMap
                    val title = dataMap.getString("title")
                    val description = dataMap.getString("description")
                    Log.d("DataLayerListener", "Received request: $title - $description")
                    // Here we would typically update a local database or a state holder
                    // and show a notification if the app is in background.
                }
            }
        }
    }

    companion object {
        private const val TAG = "DataLayerListener"
    }
}
