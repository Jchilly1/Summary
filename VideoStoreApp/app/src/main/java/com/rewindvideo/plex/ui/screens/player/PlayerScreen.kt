package com.rewindvideo.plex.ui.screens.player

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.matchParentSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import com.rewindvideo.plex.RewindApplication
import com.rewindvideo.plex.ui.components.CrtScanlineOverlay
import com.rewindvideo.plex.ui.components.RetroTvFrame
import com.rewindvideo.plex.ui.components.VcrInsertAnimation
import com.rewindvideo.plex.ui.theme.Mustard
import com.rewindvideo.plex.ui.theme.NeonPink
import com.rewindvideo.plex.ui.theme.StoreBackground

@Composable
fun PlayerScreen(ratingKey: String) {
    val context = LocalContext.current
    val app = context.applicationContext as RewindApplication
    val viewModel: PlayerViewModel = viewModel(
        factory = viewModelFactory {
            initializer { PlayerViewModel(app.repository, ratingKey) }
        },
    )
    val state by viewModel.uiState.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.Center,
    ) {
        when (state.phase) {
            PlaybackPhase.LOADING -> CircularProgressIndicator(color = Mustard)
            PlaybackPhase.ERROR -> Text(state.error ?: "Playback error", color = NeonPink)
            PlaybackPhase.INSERTING_TAPE -> VcrInsertAnimation(
                title = state.item?.title ?: "",
                onInsertComplete = viewModel::onTapeInserted,
            )
            PlaybackPhase.PLAYING -> {
                val streamUrl = state.streamUrl
                if (streamUrl == null) {
                    Text("No stream available for this tape.", color = NeonPink)
                } else {
                    TvPlayback(streamUrl = streamUrl, title = state.item?.title ?: "")
                }
            }
        }
    }
}

@Composable
private fun TvPlayback(streamUrl: String, title: String) {
    val context = LocalContext.current
    val exoPlayer = remember {
        ExoPlayer.Builder(context).build().apply {
            setMediaItem(MediaItem.fromUri(streamUrl))
            prepare()
            playWhenReady = true
        }
    }

    DisposableEffect(Unit) {
        onDispose { exoPlayer.release() }
    }

    RetroTvFrame(modifier = Modifier.fillMaxSize()) {
        AndroidView(
            modifier = Modifier.matchParentSize(),
            factory = {
                PlayerView(context).apply {
                    player = exoPlayer
                    useController = true
                }
            },
        )
        CrtScanlineOverlay(modifier = Modifier.matchParentSize())
    }
}
