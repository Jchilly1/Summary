package com.rewindvideo.plex.ui.screens.browse

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rewindvideo.plex.data.LibrarySection
import com.rewindvideo.plex.data.PlexRepository
import com.rewindvideo.plex.data.Shelf
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class BrowseUiState(
    val sections: List<LibrarySection> = emptyList(),
    val selectedSectionKey: String? = null,
    val shelves: List<Shelf> = emptyList(),
    val isLoading: Boolean = true,
    val error: String? = null,
)

class BrowseViewModel(private val repository: PlexRepository) : ViewModel() {

    private val _uiState = MutableStateFlow(BrowseUiState())
    val uiState: StateFlow<BrowseUiState> = _uiState

    init {
        loadSections()
    }

    private fun loadSections() {
        viewModelScope.launch {
            try {
                val sections = repository.getLibrarySections()
                _uiState.update { it.copy(sections = sections, isLoading = false) }
                sections.firstOrNull()?.let { selectSection(it.key) }
            } catch (t: Throwable) {
                _uiState.update { it.copy(isLoading = false, error = t.message ?: "Couldn't load your library.") }
            }
        }
    }

    fun selectSection(sectionKey: String) {
        _uiState.update { it.copy(selectedSectionKey = sectionKey, isLoading = true, error = null) }
        viewModelScope.launch {
            try {
                val shelves = repository.getShelves(sectionKey)
                _uiState.update { it.copy(shelves = shelves, isLoading = false) }
            } catch (t: Throwable) {
                _uiState.update { it.copy(isLoading = false, error = t.message ?: "Couldn't load this shelf.") }
            }
        }
    }

    fun imageUrlFor(item: com.rewindvideo.plex.data.model.PlexItem): String? =
        repository.imageUrl(item.thumb)

    fun signOut() = repository.signOut()
}
