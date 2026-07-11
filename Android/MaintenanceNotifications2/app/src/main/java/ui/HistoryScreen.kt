package com.example.maintenancenotifications.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.maintenancenotifications.repository.NotificationRepository

@Composable
fun HistoryScreen(onClick: (Int) -> Unit) {

    val lista = NotificationRepository.getAll()

    LazyColumn(
        modifier = Modifier.padding(16.dp)
    ) {

        items(lista.indices.toList()) { index ->

            val item = lista[index]

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp)
                    .clickable {
                        onClick(index)
                    },

                shape = RoundedCornerShape(14.dp),

                elevation = CardDefaults.cardElevation(6.dp)

            ) {

                Column(
                    modifier = Modifier.padding(16.dp)
                ) {

                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {

                        Icon(
                            imageVector = Icons.Default.Build,
                            contentDescription = null
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        Text(
                            text = item.title,
                            style = MaterialTheme.typography.titleMedium
                        )

                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = item.message,
                        style = MaterialTheme.typography.bodyMedium
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "${item.date}  ",
                        style = MaterialTheme.typography.bodySmall
                    )

                }

            }

        }

    }

}