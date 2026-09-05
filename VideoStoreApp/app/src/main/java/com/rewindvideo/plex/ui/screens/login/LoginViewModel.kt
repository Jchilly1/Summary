package com.rewindvideo.plex.ui.screens.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rewindvideo.plex.data.PlexRepository
import com.rewindvideo.plex.data.model.PlexResource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed interface LoginUiState {
    data object Idle : LoginUiState
    data class AwaitingBrowserLink(val code: String, val linkUrl: String) : LoginUiState
    data object DiscoveringServers : LoginUiState
    data class ChooseServer(val servers: List<PlexResource>) : LoginUiState
    data object SignedIn : LoginUiState
    data class Error(val message: String) : LoginUiState
}

class LoginViewModel(private val repository: PlexRepository) : ViewModel() {

    private val _uiState = MutableStateFlow<LoginUiState>(LoginUiState.Idle)
    val uiState: StateFlow<LoginUiState> = _uiState

    fun startSignIn() {
        viewModelScope.launch {
            try {
                val (pin, linkUrl) = repository.startAccountLink()
                _uiState.value = LoginUiState.AwaitingBrowserLink(pin.code, linkUrl)

                val linked = repository.awaitAccountLink(pin.id)
                if (!linked) {
                    _uiState.value = LoginUiState.Error("Sign-in timed out. Try again.")
                    return@launch
                }

                _uiState.value = LoginUiState.DiscoveringServers
                val servers = repository.discoverServers()
                if (servers.isEmpty()) {
                    _uiState.value = LoginUiState.Error("No Plex servers found on your account.")
                    return@launch
                }
                _uiState.value = LoginUiState.ChooseServer(servers)
            } catch (t: Throwable) {
                _uiState.value = LoginUiState.Error(t.message ?: "Something went wrong signing in.")
            }
        }
    }

    fun selectServer(resource: PlexResource) {
        try {
            repository.selectServer(resource)
            _uiState.value = LoginUiState.SignedIn
        } catch (t: Throwable) {
            _uiState.value = LoginUiState.Error(t.message ?: "Couldn't connect to that server.")
        }
    }

    fun reset() {
        _uiState.value = LoginUiState.Idle
    }
}
