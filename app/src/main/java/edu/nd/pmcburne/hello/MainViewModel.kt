package edu.nd.pmcburne.hello

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import edu.nd.pmcburne.hello.data.Location
import edu.nd.pmcburne.hello.data.LocationDatabase
import edu.nd.pmcburne.hello.data.LocationRepository
import edu.nd.pmcburne.hello.data.PlacemarksApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class CampusMapUIState(
    val selectedTag: String = "core",
    val availableTags: List<String> = emptyList(),
    val filteredLocations: List<Location> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

class MainViewModel(application: Application) : AndroidViewModel(application) {
    private val database = LocationDatabase.getInstance(application)
    private val api = PlacemarksApi.create()
    private val repository = LocationRepository(database.locationDao(), api)

    private val _uiState = MutableStateFlow(CampusMapUIState())
    val uiState: StateFlow<CampusMapUIState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            try {
                _uiState.update { it.copy(isLoading = true) }
                // Sync locations from API
                repository.syncLocations()
                
                // Load tags
                repository.getAllUniqueTags().collect { tags ->
                    _uiState.update { it.copy(availableTags = tags) }
                }
                
                // Load locations for default tag "core"
                repository.getLocationsByTag("core").collect { locations ->
                    _uiState.update { it.copy(filteredLocations = locations, isLoading = false) }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message, isLoading = false) }
            }
        }
    }

    fun selectTag(tag: String) {
        _uiState.update { it.copy(selectedTag = tag) }
        viewModelScope.launch {
            try {
                repository.getLocationsByTag(tag).collect { locations ->
                    _uiState.update { it.copy(filteredLocations = locations) }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message) }
            }
        }
    }
}