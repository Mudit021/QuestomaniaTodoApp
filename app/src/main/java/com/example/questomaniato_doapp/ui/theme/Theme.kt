package com.example.questomaniato_doapp.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
    primary = Emerald,
    secondary = Gold,
    tertiary = Crimson,
    background = DeepNavy,
    surface = DarkSlate,
    surfaceVariant = SurfaceBlue,
    onPrimary = DeepNavy,
    onSecondary = DeepNavy,
    onTertiary = TextPrimary,
    onBackground = TextPrimary,
    onSurface = TextPrimary,
    onSurfaceVariant = TextSecondary,
    outline = BorderBlue
)

@Composable
fun QuestomaniaTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography = AppTypography,
        content = content
    )
}
