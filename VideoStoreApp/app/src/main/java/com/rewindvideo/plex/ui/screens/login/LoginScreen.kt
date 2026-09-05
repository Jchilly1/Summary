package com.rewindvideo.plex.ui.screens.login

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.rewindvideo.plex.RewindApplication
import com.rewindvideo.plex.ui.theme.CreamLabel
import com.rewindvideo.plex.ui.theme.Mustard
import com.rewindvideo.plex.ui.theme.NeonPink

@Composable
fun LoginScreen(onSignedIn: () -> Unit) {
    val context = LocalContext.current
    val app = context.applicationContext as RewindApplication
    val viewModel: LoginViewModel = viewModel(
        factory = viewModelFactory {
            initializer { LoginViewModel(app.repository) }
        },
    )
    val state by viewModel.uiState.collectAsState()

    LaunchedEffect(state) {
        if (state is LoginUiState.SignedIn) onSignedIn()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        contentAlignment = Alignment.Center,
    ) {
        when (val current = state) {
            is LoginUiState.Idle -> IdleContent(onStart = viewModel::startSignIn)
            is LoginUiState.AwaitingBrowserLink -> AwaitingLinkContent(current.code, current.linkUrl)
            is LoginUiState.DiscoveringServers -> LoadingContent("Finding your Plex servers...")
            is LoginUiState.ChooseServer -> ChooseServerContent(current, onPick = viewModel::selectServer)
            is LoginUiState.Error -> ErrorContent(current.message, onRetry = viewModel::reset)
            is LoginUiState.SignedIn -> LoadingContent("Welcome back...")
        }
    }
}

@Composable
private fun IdleContent(onStart: () -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = "REWIND\nVIDEO CLUB",
            style = MaterialTheme.typography.displayLarge,
            color = Mustard,
            textAlign = TextAlign.Center,
        )
        Text(
            text = "Be Kind, Rewind.",
            style = MaterialTheme.typography.bodyLarge,
            color = CreamLabel,
            modifier = Modifier.padding(top = 8.dp, bottom = 32.dp),
        )
        Button(onClick = onStart) {
            Text("Sign In With Plex")
        }
    }
}

private fun tryOpenBrowser(context: Context, linkUrl: String): Boolean {
    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(linkUrl))
    return if (intent.resolveActivity(context.packageManager) != null) {
        context.startActivity(intent)
        true
    } else {
        false
    }
}

@Composable
private fun AwaitingLinkContent(code: String, linkUrl: String) {
    val context = LocalContext.current
    // Devices with no browser at all (many Google TV / Android TV boxes) just never get an
    // auto-opened tab -- the on-screen code is the primary path there, entered on any other
    // device at plex.tv/link.
    var browserAvailable by remember { mutableStateOf(true) }
    LaunchedEffect(linkUrl) {
        browserAvailable = tryOpenBrowser(context, linkUrl)
    }
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text("Finish signing in at plex.tv/link", style = MaterialTheme.typography.titleLarge, color = CreamLabel, textAlign = TextAlign.Center)
        Text(
            text = if (browserAvailable) {
                "If it didn't open automatically, go to plex.tv/link on any device and enter:"
            } else {
                "On your phone or computer, go to plex.tv/link and enter:"
            },
            style = MaterialTheme.typography.bodyMedium,
            color = CreamLabel,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 12.dp, bottom = 16.dp),
        )
        Text(code, style = MaterialTheme.typography.displayLarge, color = NeonPink)
        Text(
            text = "Waiting for confirmation...",
            style = MaterialTheme.typography.bodyMedium,
            color = CreamLabel,
            modifier = Modifier.padding(top = 24.dp),
        )
        CircularProgressIndicator(modifier = Modifier.padding(top = 12.dp), color = Mustard)
        if (browserAvailable) {
            OutlinedButton(
                onClick = { tryOpenBrowser(context, linkUrl) },
                modifier = Modifier.padding(top = 20.dp),
            ) {
                Text("Reopen Sign-In Page")
            }
        }
    }
}

@Composable
private fun LoadingContent(message: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        CircularProgressIndicator(color = Mustard)
        Text(message, style = MaterialTheme.typography.bodyLarge, color = CreamLabel, modifier = Modifier.padding(top = 16.dp))
    }
}

@Composable
private fun ChooseServerContent(
    state: LoginUiState.ChooseServer,
    onPick: (com.rewindvideo.plex.data.model.PlexResource) -> Unit,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth(),
    ) {
        Text("Choose a server", style = MaterialTheme.typography.titleLarge, color = CreamLabel)
        LazyColumn(
            contentPadding = PaddingValues(top = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxWidth(),
        ) {
            items(state.servers) { server ->
                Button(onClick = { onPick(server) }, modifier = Modifier.fillMaxWidth()) {
                    Text(server.name)
                }
            }
        }
    }
}

@Composable
private fun ErrorContent(message: String, onRetry: () -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(message, style = MaterialTheme.typography.bodyLarge, color = NeonPink, textAlign = TextAlign.Center)
        Button(onClick = onRetry, modifier = Modifier.padding(top = 16.dp)) {
            Text("Try Again")
        }
    }
}
