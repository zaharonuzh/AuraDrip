package com.nulp.edu.auradrip.ui.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.State
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.nulp.edu.auradrip.domain.model.PlantConfig
import com.nulp.edu.auradrip.domain.model.PlantStatus
import com.nulp.edu.auradrip.domain.repository.PlantRepository
import com.posthog.PostHog
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

    private val _plantConfig = MutableStateFlow<PlantConfig?>(null)
    val plantConfig: StateFlow<PlantConfig?> = _plantConfig.asStateFlow()

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing.asStateFlow()

    private val _isWatering = MutableStateFlow(false)
    val isWatering: StateFlow<Boolean> = _isWatering.asStateFlow()

    private val _uiEvent = Channel<String>()
    val uiEvent = _uiEvent.receiveAsFlow()

    private val _showWaterConsumption = mutableStateOf(false)
    val showWaterConsumption: State<Boolean> = _showWaterConsumption

    init {
        observePlantStatus()
        refresh()
        checkFeatureFlags()
    }

    private fun checkFeatureFlags() {
        _showWaterConsumption.value = PostHog.isFeatureEnabled("show-water-stats")
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

            PostHog.capture(
                event = "dashboard_refresh_started",
                properties = mapOf("plant_id" to plantId)
            )

            val current = _uiState.value
            if (current !is PlantUiState.Success) {
                _uiState.value = PlantUiState.Loading
            }

            val result = repository.fetchAndSavePlantStatus(plantId)
            val configResult = repository.getPlantConfig(plantId)

            if (configResult.isSuccess) {
                PostHog.capture(
                    event = "dashboard_refresh_success",
                    properties = mapOf("plant_id" to plantId)
                )
                _plantConfig.value = configResult.getOrNull()
            }
            
            if (result.isFailure && _uiState.value !is PlantUiState.Success) {
                PostHog.capture(
                    event = "dashboard_refresh_failed",
                    properties = mapOf(
                        "plant_id" to plantId,
                        "status_error" to (result.exceptionOrNull()?.message ?: "none"),
                        "config_error" to (configResult.exceptionOrNull()?.message ?: "none")
                    )
                )
                _uiState.value = PlantUiState.Error(result.exceptionOrNull()?.message ?: "Unknown error")
            }
            
            _isRefreshing.value = false
        }
    }

    fun forceWaterNow() {
        viewModelScope.launch {
            _isWatering.value = true

            PostHog.capture(
                event = "watering_attempted",
                properties = mapOf("plant_id" to plantId)
            )

            val result = repository.forceWater(plantId)
            
            if (result.isSuccess) {
                PostHog.capture(
                    event = "watering_success",
                    properties = mapOf(
                        "plant_id" to plantId,
                        "status" to "command_sent"
                    )
                )
                _uiEvent.send("command_sent")
            } else {
                PostHog.capture(
                    event = "watering_failed",
                    properties = mapOf(
                        "plant_id" to plantId,
                        "error" to (result.exceptionOrNull()?.message ?: "unknown")
                    )
                )
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
