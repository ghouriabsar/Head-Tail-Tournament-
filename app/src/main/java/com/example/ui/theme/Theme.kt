package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val HighDensityColorScheme = lightColorScheme(
    primary = HighDensityGreenHeader,
    onPrimary = Color.White,
    primaryContainer = HighDensitySurfaceVariant,
    onPrimaryContainer = HighDensityGreenHeader,
    secondary = HighDensitySecondaryPill,
    onSecondary = Color.White,
    secondaryContainer = HighDensityChipBg,
    onSecondaryContainer = HighDensityTextPrimary,
    tertiary = GoldAccent,
    background = HighDensityBg,
    onBackground = HighDensityTextPrimary,
    surface = HighDensitySurface,
    onSurface = HighDensityTextPrimary,
    surfaceVariant = HighDensitySurfaceVariant,
    onSurfaceVariant = HighDensityTextSecondary,
    outline = HighDensityBorder,
    outlineVariant = HighDensityBorderDark
)

@Composable
fun HeadAndTailTheme(
    darkTheme: Boolean = false,
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = HighDensityColorScheme,
        typography = Typography,
        content = content
    )
}

