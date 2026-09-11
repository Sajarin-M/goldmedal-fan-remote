package dev.sajarinm.fanremote.ui

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val RemoteColors =
    darkColorScheme(
        primary = Color(0xFFB4CEEF),
        onPrimary = Color(0xFF142C47),
        primaryContainer = Color(0xFF2B3E54),
        onPrimaryContainer = Color(0xFFD9E9FF),
        tertiary = Color(0xFFE4A2A0),
        background = Color(0xFF101216),
        onBackground = Color(0xFFEDF0F4),
        surface = Color(0xFF1C2027),
        onSurface = Color(0xFFEDF0F4),
        surfaceVariant = Color(0xFF2B3039),
        onSurfaceVariant = Color(0xFF9CA5B2),
        outline = Color(0xFF657080),
        outlineVariant = Color(0xFF333A45),
    )

@Composable
fun FanRemoteTheme(content: @Composable () -> Unit) {
    MaterialTheme(colorScheme = RemoteColors, content = content)
}
