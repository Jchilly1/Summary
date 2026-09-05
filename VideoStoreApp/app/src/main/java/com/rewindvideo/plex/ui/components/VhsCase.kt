package com.rewindvideo.plex.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.align
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.draw.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.rewindvideo.plex.ui.theme.CreamLabel
import com.rewindvideo.plex.ui.theme.InkBlack
import com.rewindvideo.plex.ui.theme.Mustard
import com.rewindvideo.plex.ui.theme.NeonPink
import com.rewindvideo.plex.ui.theme.Teal

data class VhsBackInfo(
    val plot: String,
    val year: Int?,
    val runtimeMinutes: Int?,
    val contentRating: String,
    val genres: List<String>,
    val cast: List<String>,
    val studio: String,
)

/**
 * A VHS case you can flip like the real thing: front is the box art, back is the
 * plot synopsis / cast / rating stamp printed the way rental tapes always were.
 */
@Composable
fun FlippableVhsCase(
    title: String,
    posterUrl: String?,
    backInfo: VhsBackInfo,
    modifier: Modifier = Modifier,
) {
    var showingBack by remember { mutableStateOf(false) }
    val rotation by animateFloatAsState(
        targetValue = if (showingBack) 180f else 0f,
        animationSpec = tween(durationMillis = 500),
        label = "vhsFlip",
    )

    Box(
        modifier = modifier
            .aspectRatio(0.68f)
            .graphicsLayer {
                rotationY = rotation
                cameraDistance = 12f * density
            }
            .clickable { showingBack = !showingBack },
    ) {
        if (rotation <= 90f) {
            VhsCaseFront(title = title, posterUrl = posterUrl)
        } else {
            Box(modifier = Modifier.graphicsLayer { rotationY = 180f }) {
                VhsCaseBack(title = title, info = backInfo)
            }
        }
    }
}

@Composable
private fun VhsCaseFront(title: String, posterUrl: String?) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .clip(RoundedCornerShape(4.dp))
            .background(InkBlack),
    ) {
        AsyncImage(
            model = posterUrl,
            contentDescription = title,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize(),
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.linearGradient(
                        colors = listOf(CreamLabel.copy(alpha = 0.18f), CreamLabel.copy(alpha = 0f)),
                    ),
                ),
        )
        Box(
            modifier = Modifier
                .width(14.dp)
                .fillMaxSize()
                .background(Mustard),
        )
        Text(
            text = "TAP TO FLIP ↻",
            style = MaterialTheme.typography.labelLarge,
            color = CreamLabel,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .background(InkBlack.copy(alpha = 0.55f))
                .padding(6.dp),
        )
    }
}

@Composable
private fun VhsCaseBack(title: String, info: VhsBackInfo) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .clip(RoundedCornerShape(4.dp))
            .background(CreamLabel)
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
    ) {
        Text(
            text = title.uppercase(),
            style = MaterialTheme.typography.titleLarge,
            color = InkBlack,
        )
        Spacer(modifier = Modifier.height(8.dp))

        Row {
            info.year?.let { RatingStamp(it.toString(), Teal) }
            Spacer(modifier = Modifier.width(6.dp))
            info.runtimeMinutes?.let { RatingStamp("${it} MIN", Teal) }
            Spacer(modifier = Modifier.width(6.dp))
            if (info.contentRating.isNotBlank()) RatingStamp(info.contentRating, NeonPink)
        }
        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = info.plot.ifBlank { "No synopsis available for this tape." },
            style = MaterialTheme.typography.bodyMedium,
            color = InkBlack,
        )
        Spacer(modifier = Modifier.height(12.dp))

        if (info.genres.isNotEmpty()) {
            Text(
                text = info.genres.joinToString(" / ").uppercase(),
                style = MaterialTheme.typography.labelLarge,
                color = InkBlack.copy(alpha = 0.7f),
            )
            Spacer(modifier = Modifier.height(10.dp))
        }

        if (info.cast.isNotEmpty()) {
            Text(
                text = "STARRING",
                style = MaterialTheme.typography.labelLarge,
                color = InkBlack.copy(alpha = 0.7f),
            )
            Text(
                text = info.cast.take(6).joinToString(", "),
                style = MaterialTheme.typography.bodyMedium,
                color = InkBlack,
            )
        }

        if (info.studio.isNotBlank()) {
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = info.studio,
                style = MaterialTheme.typography.labelLarge,
                color = InkBlack.copy(alpha = 0.6f),
            )
        }
    }
}

@Composable
private fun RatingStamp(text: String, color: androidx.compose.ui.graphics.Color) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(2.dp))
            .background(color)
            .padding(horizontal = 8.dp, vertical = 3.dp),
    ) {
        Text(text = text, style = MaterialTheme.typography.labelLarge, color = CreamLabel)
    }
}
