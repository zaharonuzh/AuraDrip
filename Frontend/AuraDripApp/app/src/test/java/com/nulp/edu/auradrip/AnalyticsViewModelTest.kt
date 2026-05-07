package com.nulp.edu.auradrip

import app.cash.turbine.test
import com.nulp.edu.auradrip.domain.model.PlantHistory
import com.nulp.edu.auradrip.domain.model.TelemetryPoint
import com.nulp.edu.auradrip.domain.repository.PlantRepository
import com.nulp.edu.auradrip.ui.viewmodel.AnalyticsViewModel
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class AnalyticsViewModelTest {

    // Тестовий диспетчер для заміни Main диспетчера
    private val testDispatcher = StandardTestDispatcher()
    private val repository: PlantRepository = mockk()
    private lateinit var viewModel: AnalyticsViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)

        val mockHistory = PlantHistory(
            periodDays = 7,
            averageMoisture = 0.0,
            averageTemperature = 0.0,
            averageAirHumidity = 0.0,
            items = emptyList<TelemetryPoint>()
        )

        // Симулюємо успішну відповідь репозиторію для будь-якого запиту
        coEvery { repository.getPlantHistory(any(), any()) } returns flowOf(mockHistory)

        viewModel = AnalyticsViewModel(repository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `setPeriod updates selectedDays in uiState and fetches new data`() = runTest {
        // Given
        val newPeriod = 30
        val mockHistoryWith30Days = PlantHistory(
            periodDays = newPeriod,
            averageMoisture = 0.0,
            averageTemperature = 0.0,
            averageAirHumidity = 0.0,
            items = emptyList<TelemetryPoint>()
        )

        coEvery { repository.getPlantHistory(any(), newPeriod) } returns flowOf(mockHistoryWith30Days)

        viewModel.uiState.test {
            skipItems(1)

            // When
            viewModel.setPeriod(newPeriod)

            // Then
            val state = awaitItem()
            assertEquals(newPeriod, state.selectedDays)

            coVerify { repository.getPlantHistory(any(), newPeriod) }
        }
    }

    @Test
    fun `initial uiState should have 14 days by default`() {
        // Перевірка дефолтного значення
        assertEquals(14, viewModel.uiState.value.selectedDays)
    }
}