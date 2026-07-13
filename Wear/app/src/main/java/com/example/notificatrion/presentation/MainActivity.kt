package com.example.notificatrion.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.*
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(Color(0xFF0F0F0F), Color.Black)
                            )
                        ),
                    contentPadding = contentPadding,
                    state = listState
                ) {
                    item {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 10.dp, bottom = 12.dp)
                                .transformedHeight(this, transformationSpec),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .background(MaterialTheme.colorScheme.primaryContainer, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Engineering,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onPrimaryContainer,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Text(
                                text = "Panel Técnico",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.ExtraBold,
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.padding(top = 4.dp)
                            )
                            Text(
                                text = "${requests.size} Pendientes",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
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
                    
                    item {
                        Spacer(modifier = Modifier.height(20.dp))
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
    val isPending = request.status == "Pending"
    val isInProgress = request.status == "In Progress"
    val isRead = request.isRead
    
    val animatedBgColor by animateColorAsState(
        targetValue = when {
            isResolved -> Color(0xFF1B5E20)
            isInProgress -> Color(0xFF0D47A1)
            isRead -> Color(0xFF252525)
            else -> MaterialTheme.colorScheme.surfaceContainer
        },
        animationSpec = tween(600)
    )

    val statusIcon = when {
        isResolved -> Icons.Default.CheckCircle
        isInProgress -> Icons.Default.Sync
        isPending && request.title.contains("URGENTE") -> Icons.Default.Report
        else -> Icons.Default.Warning
    }
    
    val statusTint = when {
        isResolved -> Color.Green
        isInProgress -> Color(0xFF64B5F6)
        isPending && request.title.contains("URGENTE") -> Color.Red
        else -> Color.Yellow
    }

    TitleCard(
        onClick = onRead,
        title = { 
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = statusIcon,
                    contentDescription = null,
                    tint = statusTint,
                    modifier = Modifier.size(16.dp).padding(end = 6.dp)
                )
                Text(
                    text = request.title,
                    maxLines = 1,
                    fontWeight = if (isRead) FontWeight.Medium else FontWeight.ExtraBold,
                    fontSize = 14.sp
                )
            }
        },
        subtitle = { 
            Text(
                text = when {
                    isResolved -> "Atendido ✅"
                    isInProgress -> "En Proceso..."
                    else -> "Pendiente ⚠️"
                },
                color = statusTint,
                style = MaterialTheme.typography.labelSmall
            )
        },
        colors = CardDefaults.cardColors(containerColor = animatedBgColor),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp, horizontal = 4.dp)
            .animateContentSize(animationSpec = spring())
            .transformedHeight(this, transformationSpec),
        transformation = SurfaceTransformation(transformationSpec),
    ) {
        Column {
            Text(
                text = request.description,
                style = MaterialTheme.typography.bodySmall,
                color = if (isRead) MaterialTheme.colorScheme.onSurfaceVariant else Color.White,
                modifier = Modifier.padding(bottom = 8.dp),
                maxLines = 2
            )
            
            if (!isResolved) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Button(
                        onClick = onResolve,
                        modifier = Modifier.weight(1f).height(36.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                    ) {
                        Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(18.dp))
                    }
                    
                    Button(
                        onClick = onIgnore,
                        modifier = Modifier.size(36.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFB00020))
                    ) {
                        Icon(Icons.Default.DeleteOutline, contentDescription = null, modifier = Modifier.size(16.dp))
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
        Box(modifier = Modifier.fillMaxSize().background(Color.Black), contentAlignment = Alignment.Center) {
            Text("Vista previa")
        }
    }
}
