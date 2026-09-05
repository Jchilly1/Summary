package com.rewindvideo.plex.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.rewindvideo.plex.ui.theme.CreamLabel
import com.rewindvideo.plex.ui.theme.InkBlack
import com.rewindvideo.plex.ui.theme.Mustard

/** A single tape standing face-out on a shelf, the way a store displays its current cover art. */
@Composable
fun VhsCaseTile(
    title: String,
    posterUrl: String?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .width(112.dp)
            .clickable(onClick = onClick),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(150.dp)
                .clip(RoundedCornerShape(2.dp))
                .background(InkBlack)
                .border(2.dp, InkBlack, RoundedCornerShape(2.dp)),
        ) {
            AsyncImage(
                model = posterUrl,
                contentDescription = title,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize(),
            )
            // Plastic case gloss highlight, top-left to bottom-right.
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.linearGradient(
                            colors = listOf(
                                CreamLabel.copy(alpha = 0.16f),
                                CreamLabel.copy(alpha = 0f),
                            ),
                        ),
                    ),
            )
            // Left edge spine strip.
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(0.dp),
            ) {
                Box(
                    modifier = Modifier
                        .width(6.dp)
                        .fillMaxSize()
                        .background(Mustard),
                )
            }
        }
        Text(
            text = title,
            style = MaterialTheme.typography.labelLarge,
            color = CreamLabel,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 4.dp),
        )
    }
}
