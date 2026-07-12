package com.example.notificatrion.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.*
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.wear.compose.foundation.lazy.TransformingLazyColumn
import androidx.wear.compose.foundation.lazy.TransformingLazyColumnItemScope
import androidx.wear.compose.foundation.lazy.items
import androidx.wear.compose.foundation.lazy.rememberTransformingLazyColumnState
import androidx.wear.compose.material3.*
import androidx.wear.compose.material3.lazy.rememberTransformationSpec
import androidx.wear.compose.material3.lazy.transformedHeight
import androidx.wear.compose.ui.tooling.preview.WearPreviewDevices
import androidx.wear.compose.ui.tooling.preview.WearPreviewFontScales
import com.example.notificatrion.R
import com.example.notificatrion.data.MaintenanceRequest
import com.example.notificatrion.presentation.theme.NotificatrionTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            WearApp()
        }
    }
}

@Composable
fun WearApp(viewModel: MaintenanceViewModel = viewModel()) {
    val requests by viewModel.requests
    
    NotificatrionTheme {
        AppScaffold {
            val listState = rememberTransformingLazyColumnState()
            val transformationSpec = rememberTransformationSpec()
            
            ScreenScaffold(
                scrollState = listState,
                timeText = { TimeText() }
            ) { contentPadding ->
                TransformingLazyColumn(
                    modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background),
                    contentPadding = contentPadding,
                    state = listState
                ) {
                    item {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 8.dp)
                                .transformedHeight(this, transformationSpec),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = Icons.Default.NotificationsActive,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(24.dp)
                            )
                            Text(
                                text = stringResource(R.string.title_requests),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(top = 4.dp)
                            )
                        }
                    }
                    
                    item {
                        AnimatedVisibility(
                            visible = requests.isEmpty(),
                            enter = fadeIn() + expandVertically(),
                            exit = fadeOut() + shrinkVertically()
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 20.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Build,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.outline,
                                    modifier = Modifier.size(40.dp)
                                )
                                Text(
                                    text = stringResource(R.string.no_requests),
                                    modifier = Modifier.padding(16.dp),
                                    style = MaterialTheme.typography.bodyMedium,
                                    textAlign = TextAlign.Center,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }

                    items(requests, key = { it.id }) { request ->
                        MaintenanceRequestCard(
                            request = request,
                            transformationSpec = transformationSpec,
                            onResolve = { viewModel.markAsResolved(request.id) },
                            onRead = { viewModel.markAsRead(request.id) },
                            onIgnore = { viewModel.ignoreRequest(request.id) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun TransformingLazyColumnItemScope.MaintenanceRequestCard(
    request: MaintenanceRequest,
    transformationSpec: androidx.wear.compose.material3.lazy.TransformationSpec,
    onResolve: () -> Unit,
    onRead: () -> Unit,
    onIgnore: () -> Unit
) {
    val isResolved = request.status == "Resolved"
    val isRead = request.isRead
    
    // Animación de color de fondo
    val animatedBgColor by animateColorAsState(
        targetValue = if (isResolved) Color(0xFF1B5E20) 
                     else if (isRead) MaterialTheme.colorScheme.surfaceContainerLow 
                     else MaterialTheme.colorScheme.surfaceContainer,
        animationSpec = tween(durationMillis = 500)
    )

    val cardColor = CardDefaults.cardColors(
        containerColor = animatedBgColor,
        contentColor = if (isResolved) Color.White else MaterialTheme.colorScheme.onSurface
    )

    val statusIcon = if (isResolved) Icons.Default.CheckCircle else Icons.Default.Warning
    val statusTint = if (isResolved) Color.Green else if (isRead) Color.Gray else Color.Yellow

    TitleCard(
        onClick = onRead,
        title = { 
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = statusIcon,
                    contentDescription = null,
                    tint = statusTint,
                    modifier = Modifier.size(16.dp).padding(end = 4.dp)
                )
                Text(
                    text = request.title,
                    maxLines = 1,
                    fontWeight = if (isRead) FontWeight.Normal else FontWeight.Bold
                )
            }
        },
        subtitle = { 
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = if (isResolved) "Atendido" else if (isRead) "Leído" else "Pendiente",
                    color = if (isResolved) Color.Green else if (isRead) Color.Gray else Color.Yellow
                )
                if (isRead) {
                    Spacer(Modifier.width(4.dp))
                    Icon(Icons.Default.DoneAll, contentDescription = null, modifier = Modifier.size(14.dp), tint = Color.Cyan)
                }
            }
        },
        colors = cardColor,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .animateContentSize(animationSpec = spring())
            .transformedHeight(this, transformationSpec),
        transformation = SurfaceTransformation(transformationSpec),
    ) {
        Column {
            Text(
                text = request.description,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(bottom = 8.dp),
                maxLines = 2
            )
            
            AnimatedVisibility(
                visible = true, // Siempre visible pero para que el layout anime cambios internos
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    if (!isResolved) {
                        Button(
                            onClick = onResolve,
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                        ) {
                            Icon(Icons.Default.Done, contentDescription = null, modifier = Modifier.size(18.dp))
                        }
                    }
                    
                    Button(
                        onClick = onIgnore,
                        modifier = Modifier.weight(0.5f),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.errorContainer)
                    ) {
                        Icon(Icons.Default.Delete, contentDescription = "Ignorar", modifier = Modifier.size(18.dp))
                    }
                }
            }
        }
    }
}

@WearPreviewDevices
@WearPreviewFontScales
@Composable
fun DefaultPreview() {
    NotificatrionTheme {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Vista previa")
        }
    }
}
