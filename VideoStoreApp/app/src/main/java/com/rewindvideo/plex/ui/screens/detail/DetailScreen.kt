package com.rewindvideo.plex.ui.screens.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.weight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.rewindvideo.plex.RewindApplication
import com.rewindvideo.plex.data.model.PlexItem
import com.rewindvideo.plex.ui.components.FlippableVhsCase
import com.rewindvideo.plex.ui.components.VhsBackInfo
import com.rewindvideo.plex.ui.theme.CreamLabel
import com.rewindvideo.plex.ui.theme.Mustard
import com.rewindvideo.plex.ui.theme.NeonPink
import com.rewindvideo.plex.ui.theme.StoreBackground
import com.rewindvideo.plex.ui.theme.StoreSurface

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailScreen(
    ratingKey: String,
    onBack: () -> Unit,
    onPlay: (PlexItem) -> Unit,
) {
    val context = LocalContext.current
    val app = context.applicationContext as RewindApplication
    val viewModel: DetailViewModel = viewModel(
        factory = viewModelFactory {
            initializer { DetailViewModel(app.repository, ratingKey) }
        },
    )
    val state by viewModel.uiState.collectAsState()

    Scaffold(
        containerColor = StoreBackground,
        topBar = {
            TopAppBar(
                title = { Text(state.item?.title ?: "") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
            )
        },
    ) { padding ->
        when {
            state.isLoading -> Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = Mustard)
            }
            state.error != null -> Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                Text(state.error ?: "")
            }
            state.item != null -> {
                val item = state.item!!
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentPadding = PaddingValues(16.dp),
                ) {
                    item {
                        FlippableVhsCase(
                            title = item.title,
                            posterUrl = viewModel.imageUrlFor(item),
                            backInfo = VhsBackInfo(
                                plot = item.summary,
                                year = item.year,
                                runtimeMinutes = if (item.duration > 0) (item.duration / 60000).toInt() else null,
                                contentRating = item.contentRating,
                                genres = item.genres.map { it.tag },
                                cast = item.roles.map { it.tag },
                                studio = item.studio,
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 20.dp),
                        )
                    }

                    if (item.isMovie) {
                        item {
                            Button(
                                onClick = { onPlay(item) },
                                modifier = Modifier.fillMaxWidth(),
                            ) {
                                Icon(Icons.Filled.PlayArrow, contentDescription = null)
                                Text("  Play Tape")
                            }
                        }
                    }

                    if (item.isShow) {
                        item {
                            Text(
                                "SEASONS",
                                style = MaterialTheme.typography.titleMedium,
                                color = Mustard,
                                modifier = Modifier.padding(top = 12.dp, bottom = 8.dp),
                            )
                            LazyRow {
                                items(state.seasons, key = { it.ratingKey }) { season ->
                                    val selected = season.ratingKey == state.selectedSeasonKey
                                    Box(
                                        modifier = Modifier
                                            .padding(end = 8.dp)
                                            .clip(RoundedCornerShape(4.dp))
                                            .background(if (selected) Mustard else StoreSurface)
                                            .clickable { viewModel.selectSeason(season.ratingKey) }
                                            .padding(horizontal = 14.dp, vertical = 10.dp),
                                    ) {
                                        Text(
                                            season.title,
                                            color = if (selected) StoreBackground else CreamLabel,
                                        )
                                    }
                                }
                            }
                        }
                        items(state.episodes, key = { it.ratingKey }) { episode ->
                            EpisodeRow(episode = episode, onPlay = { onPlay(episode) })
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun EpisodeRow(episode: PlexItem, onPlay: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
            .clip(RoundedCornerShape(6.dp))
            .background(StoreSurface)
            .clickable(onClick = onPlay)
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "${episode.index?.let { "$it. " } ?: ""}${episode.title}",
                style = MaterialTheme.typography.titleMedium,
                color = CreamLabel,
            )
            if (episode.summary.isNotBlank()) {
                Text(
                    text = episode.summary,
                    style = MaterialTheme.typography.bodyMedium,
                    color = CreamLabel.copy(alpha = 0.7f),
                    maxLines = 2,
                )
            }
        }
        Icon(Icons.Filled.PlayArrow, contentDescription = "Play", tint = NeonPink)
    }
}
