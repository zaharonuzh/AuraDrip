package com.nulp.edu.auradrip.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.nulp.edu.auradrip.domain.model.PlantStatus
import com.nulp.edu.auradrip.domain.repository.PlantRepository
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

sealed class PlantUiState {
    object Loading : PlantUiState()
    data class Success(val data: PlantStatus) : PlantUiState()
    data class Error(val message: String) : PlantUiState()
}

class PlantViewModel(
    private val repository: PlantRepository,
    private val plantId: Int = 1 // Default to 1 for demo
) : ViewModel() {

    private val _uiState = MutableStateFlow<PlantUiState>(PlantUiState.Loading)
    val uiState: StateFlow<PlantUiState> = _uiState.asStateFlow()

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing.asStateFlow()

    private val _isWatering = MutableStateFlow(false)
    val isWatering: StateFlow<Boolean> = _isWatering.asStateFlow()

    private val _uiEvent = Channel<String>()
    val uiEvent = _uiEvent.receiveAsFlow()

    init {
        observePlantStatus()
        refresh()
    }

    private fun observePlantStatus() {
        repository.getPlantStatus(plantId)
            .onEach { plant ->
                if (plant != null) {
                    _uiState.value = PlantUiState.Success(plant)
                }
            }
            .launchIn(viewModelScope)
    }

    fun refresh() {
        viewModelScope.launch {
            _isRefreshing.value = true
            val current = _uiState.value
            if (current !is PlantUiState.Success) {
                _uiState.value = PlantUiState.Loading
            }

            val result = repository.fetchAndSavePlantStatus(plantId)
            
            if (result.isFailure && _uiState.value !is PlantUiState.Success) {
                _uiState.value = PlantUiState.Error(result.exceptionOrNull()?.message ?: "Unknown error")
            }
            
            _isRefreshing.value = false
        }
    }

    fun forceWaterNow() {
        viewModelScope.launch {
            _isWatering.value = true
            val result = repository.forceWater(plantId)
            
            if (result.isSuccess) {
                _uiEvent.send("command_sent")
            } else {
                _uiEvent.send(result.exceptionOrNull()?.message ?: "error")
            }
            
            _isWatering.value = false
        }
    }

    companion object {
        fun provideFactory(
            repository: PlantRepository,
            plantId: Int = 1
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return PlantViewModel(repository, plantId) as T
            }
        }
    }
}
