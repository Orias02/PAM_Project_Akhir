package com.example.ticketapp.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary = SageGreenDark,
    onPrimary = White,
    primaryContainer = SageGreenContainer,
    onPrimaryContainer = SageGreenDark,
    secondary = NavyDark,
    onSecondary = White,
    secondaryContainer = LightGray,
    onSecondaryContainer = NavyDark,
    tertiary = SageGreen,
    onTertiary = White,
    background = White,
    onBackground = NavyDark,
    surface = White,
    onSurface = NavyDark,
    surfaceVariant = SageGreenContainer,
    onSurfaceVariant = DarkGray,
    error = ErrorRed,
    onError = White,
    outline = LightGray,
    outlineVariant = MediumGray,
)

private val DarkColorScheme = darkColorScheme(
    primary = SageGreen,
    onPrimary = NavyDark,
    primaryContainer = SageGreenDark,
    onPrimaryContainer = SageGreenLight,
    secondary = SageGreenLight,
    onSecondary = NavyDark,
    background = Color(0xFF1A1E2E),
    onBackground = White,
    surface = Color(0xFF252A3D),
    onSurface = White,
    error = ErrorRed,
    onError = White,
)

@Composable
fun TicketAppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.background.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

// Needed for Color reference in DarkColorScheme
