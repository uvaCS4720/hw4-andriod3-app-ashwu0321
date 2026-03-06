package edu.nd.pmcburne.hello

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class MainUIState(
    val counterValue: Int
)

class MainViewModel(
    val initialCounterValue: Int = 0
) : ViewModel() {
    private val _uiState = MutableStateFlow(MainUIState(initialCounterValue))
    val uiState: StateFlow<MainUIState> = _uiState.asStateFlow()

    fun incrementCounter() {
        _uiState.update{ currentState ->
            currentState.copy(counterValue = _uiState.value.counterValue + 1)
        }
    }

    fun decrementCounter() {
        _uiState.update{ currentState ->
            currentState.copy(counterValue = _uiState.value.counterValue - 1)
        }
    }

    fun resetCounter() {
        _uiState.update { currentState ->
            currentState.copy(counterValue = 0)
        }
    }

    val isDecrementEnabled: Boolean
        get() = _uiState.value.counterValue > 0
    val isResetEnabled: Boolean
        get() = _uiState.value.counterValue > 0
}