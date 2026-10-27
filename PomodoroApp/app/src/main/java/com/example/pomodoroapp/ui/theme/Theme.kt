package com.example.pomodoroapp.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val PomodoroColors = darkColorScheme(
    primary = Color.White,
    onPrimary = Color.Black,
    secondary = Tomato,
    onSecondary = Color.White,
    tertiary = Tomato,
    background = Background,
    onBackground = TextPrimary,
    surface = SurfaceDark,
    onSurface = TextPrimary,
    surfaceVariant = SurfaceHigh,
    onSurfaceVariant = TextSecondary,
    surfaceContainerHigh = SurfaceDark,
    outline = Divider,
    error = ErrorRed
)

@Composable
fun PomodoroAppTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = PomodoroColors,
        typography = Typography,
        content = content
    )
}
