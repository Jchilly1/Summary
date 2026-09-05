package com.rewindvideo.plex.ui.screens.player

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rewindvideo.plex.data.PlexRepository
import com.rewindvideo.plex.data.model.PlexItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

enum class PlaybackPhase { LOADING, INSERTING_TAPE, PLAYING, ERROR }

data class PlayerUiState(
    val item: PlexItem? = null,
    val streamUrl: String? = null,
    val phase: PlaybackPhase = PlaybackPhase.LOADING,
    val error: String? = null,
)

class PlayerViewModel(private val repository: PlexRepository, private val ratingKey: String) : ViewModel() {

    private val _uiState = MutableStateFlow(PlayerUiState())
    val uiState: StateFlow<PlayerUiState> = _uiState

    init {
        viewModelScope.launch {
            try {
                val item = repository.getMetadata(ratingKey)
                val url = repository.streamUrl(item)
                _uiState.update { it.copy(item = item, streamUrl = url, phase = PlaybackPhase.INSERTING_TAPE) }
            } catch (t: Throwable) {
                _uiState.update { it.copy(phase = PlaybackPhase.ERROR, error = t.message ?: "Couldn't load this tape.") }
            }
        }
    }

    fun onTapeInserted() {
        _uiState.update { it.copy(phase = PlaybackPhase.PLAYING) }
    }
}
