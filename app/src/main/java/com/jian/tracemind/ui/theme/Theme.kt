package com.jian.tracemind.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = BrandTeal,
    onPrimary = Color.White,
    secondary = PurpleGrey80,
    tertiary = Pink80,
    background = Color(0xFF000000), // iOS style pure black
    surface = Color(0xFF1C1C1E),    // Distinct card background
    onBackground = Color(0xFFFFFFFF),
    onSurface = Color(0xFFF5F5F5),
    onSurfaceVariant = Color(0xFFA1A1A6),
    surfaceVariant = Color(0xFF2C2C2E)
)

private val LightColorScheme = lightColorScheme(
    primary = BrandTeal,
    onPrimary = Color.White,
    secondary = PurpleGrey40,
    tertiary = Pink40,
    background = Color(0xFFF8F9FA), // User's original sleek background
    surface = Color.White,          // User's original card background
    onBackground = Color(0xFF1A1C1E),
    onSurface = Color(0xFF1A1C1E),
    onSurfaceVariant = Color(0xFF9CA3AF),
    surfaceVariant = Color(0xFFF3F4F6)
)

@Composable
fun TraceMindTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}