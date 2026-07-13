package com.example.notificatrion.presentation.theme

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.wear.compose.material3.ColorScheme
import androidx.wear.compose.material3.MaterialTheme

private val WearColorScheme = ColorScheme(
    primary = Color(0xFFBB86FC),
    onPrimary = Color.Black,
    primaryContainer = Color(0xFF3700B3),
    onPrimaryContainer = Color.White,
    secondary = Color(0xFF03DAC6),
    onSecondary = Color.Black,
    secondaryContainer = Color(0xFF018786),
    onSecondaryContainer = Color.White,
    tertiary = Color(0xFFCF6679),
    onTertiary = Color.Black,
    tertiaryContainer = Color(0xFFB00020),
    onTertiaryContainer = Color.White,
    surfaceContainer = Color(0xFF121212),
    onSurface = Color.White,
    onSurfaceVariant = Color(0xFFBDBDBD),
    outline = Color(0xFF757575),
    background = Color.Black,
    onBackground = Color.White,
    error = Color(0xFFCF6679),
    onError = Color.Black,
    errorContainer = Color(0xFFB00020),
    onErrorContainer = Color.White,
)

@Composable
fun NotificatrionTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = WearColorScheme,
        content = content
    )
}