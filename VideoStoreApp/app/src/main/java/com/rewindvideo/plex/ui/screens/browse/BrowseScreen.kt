package com.rewindvideo.plex.ui.screens.browse

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.rewindvideo.plex.RewindApplication
import com.rewindvideo.plex.data.model.PlexItem
import com.rewindvideo.plex.ui.components.VideoShelf
import com.rewindvideo.plex.ui.theme.Mustard
import com.rewindvideo.plex.ui.theme.StoreBackground

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BrowseScreen(
    onItemClick: (PlexItem) -> Unit,
    onSignedOut: () -> Unit,
) {
    val context = LocalContext.current
    val app = context.applicationContext as RewindApplication
    val viewModel: BrowseViewModel = viewModel(
        factory = viewModelFactory {
            initializer { BrowseViewModel(app.repository) }
        },
    )
    val state by viewModel.uiState.collectAsState()

    Scaffold(
        containerColor = StoreBackground,
        topBar = {
            TopAppBar(
                title = { Text("REWIND VIDEO CLUB") },
                actions = {
                    IconButton(onClick = { viewModel.signOut(); onSignedOut() }) {
                        Icon(Icons.Filled.ExitToApp, contentDescription = "Sign out")
                    }
                },
            )
        },
    ) { padding ->
        Column(modifier = Modifier.padding(padding)) {
            if (state.sections.size > 1) {
                val selectedIndex = state.sections.indexOfFirst { it.key == state.selectedSectionKey }.coerceAtLeast(0)
                ScrollableTabRow(selectedTabIndex = selectedIndex, containerColor = StoreBackground) {
                    state.sections.forEachIndexed { index, section ->
                        Tab(
                            selected = index == selectedIndex,
                            onClick = { viewModel.selectSection(section.key) },
                            text = { Text(section.title) },
                        )
                    }
                }
            }

            when {
                state.isLoading -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = Mustard)
                }
                state.error != null -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(state.error ?: "", style = MaterialTheme.typography.bodyLarge)
                }
                state.shelves.isEmpty() -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Nothing on the shelves yet.", style = MaterialTheme.typography.bodyLarge)
                }
                else -> LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(vertical = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(18.dp),
                ) {
                    items(state.shelves, key = { it.genre }) { shelf ->
                        VideoShelf(
                            genre = shelf.genre,
                            items = shelf.items,
                            imageUrlFor = viewModel::imageUrlFor,
                            onItemClick = onItemClick,
                        )
                    }
                }
            }
        }
    }
}
