package com.rewindvideo.plex.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.align
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.lerp
import com.rewindvideo.plex.ui.theme.CreamLabel
import com.rewindvideo.plex.ui.theme.InkBlack
import com.rewindvideo.plex.ui.theme.Mustard
import com.rewindvideo.plex.ui.theme.NeonPink
import com.rewindvideo.plex.ui.theme.ShelfWoodDark
import com.rewindvideo.plex.ui.theme.TapeBrown
import kotlinx.coroutines.delay

/**
 * "Loads" the tape into a drawn VCR: the cassette slides down into the deck's slot,
 * a light blinks, then [onInsertComplete] fires so the caller can cut to playback.
 */
@Composable
fun VcrInsertAnimation(
    title: String,
    modifier: Modifier = Modifier,
    onInsertComplete: () -> Unit,
) {
    val insertProgress = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        delay(350)
        insertProgress.animateTo(1f, animationSpec = tween(durationMillis = 1100, easing = FastOutSlowInEasing))
        delay(400)
        onInsertComplete()
    }

    val progress = insertProgress.value
    val tapeOffsetY = lerp((-120).dp, 46.dp, progress.coerceIn(0f, 1f))
    val tapeAlpha = 1f - ((progress - 0.82f).coerceIn(0f, 0.18f) / 0.18f)

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(300.dp),
        contentAlignment = Alignment.TopCenter,
    ) {
        VcrBody(isLoading = progress in 0.02f..0.98f)

        Box(
            modifier = Modifier
                .offset(y = tapeOffsetY)
                .alpha(tapeAlpha)
                .width(120.dp)
                .height(74.dp)
                .clip(RoundedCornerShape(3.dp))
                .background(TapeBrown),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(26.dp)
                    .background(CreamLabel)
                    .align(Alignment.TopCenter),
            )
            Reel(modifier = Modifier.align(Alignment.BottomStart).offset(x = 16.dp, y = (-10).dp))
            Reel(modifier = Modifier.align(Alignment.BottomEnd).offset(x = (-16).dp, y = (-10).dp))
        }

        Text(
            text = if (progress < 1f) "LOADING TAPE..." else "PLAY ►",
            style = MaterialTheme.typography.labelLarge,
            color = if (progress < 1f) CreamLabel else NeonPink,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .offset(y = (-14).dp),
        )
    }
}

@Composable
private fun Reel(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .size(16.dp)
            .clip(CircleShape)
            .background(InkBlack),
    )
}

@Composable
private fun VcrBody(isLoading: Boolean) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(150.dp),
    ) {
        Canvas(modifier = Modifier.fillMaxWidth().height(150.dp)) {
            val cornerRadius = 14.dp.toPx()
            drawRoundRect(
                brush = Brush.verticalGradient(listOf(Color(0xFF2E2E33), Color(0xFF141416))),
                cornerRadius = androidx.compose.ui.geometry.CornerRadius(cornerRadius, cornerRadius),
                size = size,
            )
            // Slot mouth.
            val slotWidth = size.width * 0.42f
            val slotHeight = 20.dp.toPx()
            drawRoundRect(
                color = Color.Black,
                topLeft = Offset((size.width - slotWidth) / 2f, 34.dp.toPx()),
                size = Size(slotWidth, slotHeight),
                cornerRadius = androidx.compose.ui.geometry.CornerRadius(4.dp.toPx(), 4.dp.toPx()),
            )
            // Front control strip.
            drawRect(
                color = ShelfWoodDark,
                topLeft = Offset(0f, size.height - 34.dp.toPx()),
                size = Size(size.width, 34.dp.toPx()),
            )
        }
        Box(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .offset(x = 18.dp, y = (-12).dp)
                .size(10.dp)
                .clip(CircleShape)
                .background(if (isLoading) NeonPink else Mustard),
        )
        Text(
            text = "REWIND  •  VCR-3000",
            style = MaterialTheme.typography.labelLarge,
            color = CreamLabel.copy(alpha = 0.6f),
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .offset(x = (-14).dp, y = (-12).dp),
        )
    }
}
