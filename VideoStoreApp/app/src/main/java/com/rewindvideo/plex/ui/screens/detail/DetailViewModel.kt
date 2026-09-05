package com.rewindvideo.plex.ui.screens.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rewindvideo.plex.data.PlexRepository
import com.rewindvideo.plex.data.model.PlexItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class DetailUiState(
    val item: PlexItem? = null,
    val seasons: List<PlexItem> = emptyList(),
    val selectedSeasonKey: String? = null,
    val episodes: List<PlexItem> = emptyList(),
    val isLoading: Boolean = true,
    val error: String? = null,
)

class DetailViewModel(private val repository: PlexRepository, private val ratingKey: String) : ViewModel() {

    private val _uiState = MutableStateFlow(DetailUiState())
    val uiState: StateFlow<DetailUiState> = _uiState

    init {
        viewModelScope.launch {
            try {
                val item = repository.getMetadata(ratingKey)
                _uiState.update { it.copy(item = item, isLoading = false) }
                if (item.isShow) {
                    val seasons = repository.getChildren(ratingKey)
                    _uiState.update { it.copy(seasons = seasons) }
                    seasons.firstOrNull()?.let { selectSeason(it.ratingKey) }
                }
            } catch (t: Throwable) {
                _uiState.update { it.copy(isLoading = false, error = t.message ?: "Couldn't load this tape.") }
            }
        }
    }

    fun selectSeason(seasonRatingKey: String) {
        _uiState.update { it.copy(selectedSeasonKey = seasonRatingKey) }
        viewModelScope.launch {
            try {
                val episodes = repository.getChildren(seasonRatingKey)
                _uiState.update { it.copy(episodes = episodes) }
            } catch (t: Throwable) {
                _uiState.update { it.copy(error = t.message ?: "Couldn't load episodes.") }
            }
        }
    }

    fun imageUrlFor(item: PlexItem): String? =
        repository.imageUrl(item.thumb ?: item.art)
}
