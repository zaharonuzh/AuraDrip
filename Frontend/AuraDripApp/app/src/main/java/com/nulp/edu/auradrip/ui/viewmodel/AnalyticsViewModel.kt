package com.nulp.edu.auradrip.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.nulp.edu.auradrip.domain.model.PlantHistory
import com.nulp.edu.auradrip.domain.repository.PlantRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class AnalyticsUiState(
    val isLoading: Boolean = false,
    val history: PlantHistory? = null,
    val selectedDays: Int = 14,
    val isCombinedMode: Boolean = true,
    val errorMessage: String? = null
)

class AnalyticsViewModel(
    private val repository: PlantRepository,
    private val plantId: Int = 1
) : ViewModel() {

    private val _selectedDays = MutableStateFlow(14)
    private val _isCombinedMode = MutableStateFlow(true)
    private val _isLoading = MutableStateFlow(false)

    // Об'єднуємо всі стани в один для зручності екрана
    @OptIn(ExperimentalCoroutinesApi::class)
    val uiState: StateFlow<AnalyticsUiState> = combine(
        _selectedDays,
        _isCombinedMode,
        _isLoading
    ) { days, combined, loading ->
        Triple(days, combined, loading)
    }.flatMapLatest { (days, combined, loading) ->
        repository.getPlantHistory(plantId, days).map { history ->
            AnalyticsUiState(
                isLoading = loading,
                history = history,
                selectedDays = days,
                isCombinedMode = combined
            )
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = AnalyticsUiState(isLoading = true)
    )

    private val _uiEvent = Channel<String>()
    val uiEvent = _uiEvent.receiveAsFlow()

    fun setPeriod(days: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            _selectedDays.value = days
            // Дані оновляться автоматично через flatMapLatest
            _isLoading.value = false
        }
    }

    fun toggleChartMode() {
        _isCombinedMode.value = !_isCombinedMode.value
    }

    companion object {
        fun provideFactory(
            repository: PlantRepository,
            plantId: Int = 1
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return AnalyticsViewModel(repository, plantId) as T
            }
        }
    }
}