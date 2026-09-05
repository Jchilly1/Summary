package com.rewindvideo.plex.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

/** Faint scanlines + a vignette, drawn on top of the video, to sell the "old TV" look. */
@Composable
fun CrtScanlineOverlay(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val lineSpacing = 4.dp.toPx()
        var y = 0f
        while (y < size.height) {
            drawRect(
                color = Color.Black.copy(alpha = 0.10f),
                topLeft = Offset(0f, y),
                size = Size(size.width, lineSpacing / 2f),
            )
            y += lineSpacing
        }
        drawRect(
            brush = Brush.radialGradient(
                colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.38f)),
                center = center,
                radius = size.maxDimension * 0.75f,
            ),
        )
    }
}
