package com.rewindvideo.plex.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.rewindvideo.plex.data.model.PlexItem
import com.rewindvideo.plex.ui.theme.Mustard
import com.rewindvideo.plex.ui.theme.ShelfWood
import com.rewindvideo.plex.ui.theme.ShelfWoodDark

/** A single video-store aisle: a wood-grain shelf holding one genre's worth of tapes. */
@Composable
fun VideoShelf(
    genre: String,
    items: List<PlexItem>,
    imageUrlFor: (PlexItem) -> String?,
    onItemClick: (PlexItem) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = genre.uppercase(),
            style = MaterialTheme.typography.titleMedium,
            color = Mustard,
            modifier = Modifier.padding(start = 16.dp, bottom = 6.dp),
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(colors = listOf(ShelfWood, ShelfWoodDark)),
                ),
        ) {
            Column {
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 14.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    items(items, key = { it.ratingKey }) { item ->
                        VhsCaseTile(
                            title = item.title,
                            posterUrl = imageUrlFor(item),
                            onClick = { onItemClick(item) },
                        )
                    }
                }
                ShelfPlank()
            }
        }
    }
}

/** The wooden lip along the bottom edge of the shelf that the tapes rest on. */
@Composable
private fun ShelfPlank() {
    Canvas(
        modifier = Modifier
            .fillMaxWidth()
            .height(10.dp),
    ) {
        drawRect(
            brush = Brush.verticalGradient(
                colors = listOf(ShelfWoodDark, Color.Black),
            ),
            topLeft = Offset.Zero,
            size = size,
        )
    }
}
