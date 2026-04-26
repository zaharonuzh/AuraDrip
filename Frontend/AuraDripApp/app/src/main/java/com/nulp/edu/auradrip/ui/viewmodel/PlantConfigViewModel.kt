package com.nulp.edu.auradrip.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.nulp.edu.auradrip.data.remote.UpdateConfigDto
import com.nulp.edu.auradrip.domain.repository.PlantRepository
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class PlantConfigUiState(
    val isLoading: Boolean = false,
    val plantName: String = "",
    val controlMode: Int = 1,
    val manualThreshold: String = ""
)

class PlantConfigViewModel(
    private val repository: PlantRepository,
    private val plantId: Int
) : ViewModel() {

    private val _uiState = MutableStateFlow(PlantConfigUiState(isLoading = true))
    val uiState: StateFlow<PlantConfigUiState> = _uiState.asStateFlow()

    private val _uiEvent = Channel<String>()
    val uiEvent = _uiEvent.receiveAsFlow()

    init {
        loadConfig()
    }

    private fun loadConfig() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val result = repository.getPlantConfig(plantId)
            if (result.isSuccess) {
                val config = result.getOrThrow()
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        plantName = config.plantName,
                        controlMode = config.controlMode,
                        manualThreshold = config.manualThreshold?.toString() ?: ""
                    )
                }
            } else {
                _uiState.update { it.copy(isLoading = false) }
                _uiEvent.send(result.exceptionOrNull()?.message ?: "error")
            }
        }
    }

    fun updateOperatingMode(mode: Int) {
        _uiState.update { it.copy(controlMode = mode) }
    }

    fun updateThreshold(threshold: String) {
        _uiState.update { it.copy(manualThreshold = threshold) }
    }

    fun saveConfig() {
        viewModelScope.launch {
            val currentState = _uiState.value
            _uiState.update { it.copy(isLoading = true) }
            
            val thresholdInt = currentState.manualThreshold.toIntOrNull()
            val dto = UpdateConfigDto(
                controlMode = currentState.controlMode,
                manualThreshold = if (currentState.controlMode == 3) thresholdInt else null
            )
            
            val result = repository.updatePlantConfig(plantId, dto)
            if (result.isSuccess) {
                _uiEvent.send("save_success")
            } else {
                _uiEvent.send(result.exceptionOrNull()?.message ?: "error")
            }
            _uiState.update { it.copy(isLoading = false) }
        }
    }

    companion object {
        fun provideFactory(
            repository: PlantRepository,
            plantId: Int
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return PlantConfigViewModel(repository, plantId) as T
            }
        }
    }
}
