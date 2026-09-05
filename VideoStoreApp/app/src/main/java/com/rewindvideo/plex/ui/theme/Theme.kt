package com.rewindvideo.plex.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val RewindColorScheme = darkColorScheme(
    primary = Mustard,
    onPrimary = InkBlack,
    secondary = Teal,
    onSecondary = CreamLabel,
    tertiary = NeonPink,
    background = StoreBackground,
    onBackground = CreamLabel,
    surface = StoreSurface,
    onSurface = CreamLabel,
    surfaceVariant = ShelfWoodDark,
    error = NeonPink,
)

@Composable
fun RewindVideoClubTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = RewindColorScheme,
        typography = RewindTypography,
        content = content,
    )
}
