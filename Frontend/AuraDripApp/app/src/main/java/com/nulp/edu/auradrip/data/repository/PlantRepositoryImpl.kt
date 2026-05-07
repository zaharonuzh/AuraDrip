package com.nulp.edu.auradrip.data.repository

import com.nulp.edu.auradrip.data.local.PlantDao
import com.nulp.edu.auradrip.data.local.PlantStatusEntity
import com.nulp.edu.auradrip.data.remote.ActionResponse
import com.nulp.edu.auradrip.data.remote.PlantApi
import com.nulp.edu.auradrip.data.remote.UpdateConfigDto
import com.nulp.edu.auradrip.domain.model.PlantConfig
import com.nulp.edu.auradrip.domain.model.PlantStatus
import com.nulp.edu.auradrip.domain.repository.PlantRepository
import com.nulp.edu.auradrip.domain.model.PlantHistory
import com.nulp.edu.auradrip.domain.model.TelemetryPoint
import com.nulp.edu.auradrip.data.local.PlantHistoryEntity
import com.nulp.edu.auradrip.data.remote.PlantHistoryItemDto
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import java.time.Instant

class PlantRepositoryImpl(
    private val plantDao: PlantDao,
    private val plantApi: PlantApi
) : PlantRepository {

    override fun getPlantStatus(plantId: Int): Flow<PlantStatus?> {
        return plantDao.getPlantStatus(plantId).map { entity ->
            entity?.let {
                PlantStatus(
                    plantId = it.plantId,
                    ageDays = it.ageDays,
                    currentMoisture = it.currentMoisture,
                    currentTemp = it.currentTemp,
                    currentAirHum = it.currentAirHum,
                    lastUpdate = it.lastUpdate
                )
            }
        }
    }

    override suspend fun fetchAndSavePlantStatus(plantId: Int): Result<Unit> {
        return try {
            val response = plantApi.getPlantStatus(plantId)
            val entity = PlantStatusEntity(
                plantId = plantId,
                ageDays = response.ageDays,
                currentMoisture = response.currentMoisture,
                currentTemp = response.currentTemp,
                currentAirHum = response.currentAirHum,
                lastUpdate = response.lastUpdate
            )
            plantDao.insertPlantStatus(entity)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun forceWater(plantId: Int): Result<ActionResponse> {
        return try {
            val response = plantApi.forceWater(plantId)
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getPlantConfig(plantId: Int): Result<PlantConfig> {
        return try {
            val response = plantApi.getPlantConfig(plantId)
            Result.success(
                PlantConfig(
                    plantId = response.plantId,
                    plantName = response.plantName,
                    controlMode = response.controlMode,
                    minMoistureThreshold = response.minMoistureThreshold
                )
            )
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updatePlantConfig(plantId: Int, configDto: UpdateConfigDto): Result<ActionResponse> {
        return try {
            val response = plantApi.updatePlantConfig(plantId, configDto)
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun getPlantHistory(plantId: Int, days: Int): Flow<PlantHistory?> = flow {
        // Спочатку намагаємося оновити дані в базі з мережі
        try {
            val response = plantApi.getPlantHistory(plantId, days)

            // Використовуємо транзакційність: видаляємо старе, записуємо нове
            plantDao.clearHistoryForPlant(plantId)
            plantDao.insertHistory(response.history.map { it.toEntity(plantId) })
        } catch (e: Exception) {
            // Якщо помилка (немає інету), ми просто ігноруємо її.
            // Flow нижче все одно видасть те, що було в базі раніше.
            android.util.Log.e("AuraDripRepo", "Error fetching history: ${e.message}")
        }

        // Транслюємо дані з бази в UI
        emitAll(plantDao.getHistoryForPlant(plantId).map { entities ->
            if (entities.isEmpty()) return@map null

            PlantHistory(
                periodDays = days,
                averageMoisture = entities.map { it.soilMoisture }.average(),
                averageTemperature = entities.map { it.airTemperature }.average(),
                averageAirHumidity = entities.map { it.airHumidity }.average(),
                items = entities.map { it.toDomain() }
            )
        })
    }

    private fun PlantHistoryItemDto.toEntity(plantId: Int) = PlantHistoryEntity(
        plantId = plantId,
        timestamp = this.timestamp.toEpochMilli(),
        soilMoisture = this.soilMoisture.toFloat(),
        airTemperature = this.airTemperature.toFloat(),
        airHumidity = this.airHumidity.toFloat()
    )

    private fun PlantHistoryEntity.toDomain() = TelemetryPoint(
        timestamp = Instant.ofEpochMilli(this.timestamp),
        soilMoisture = this.soilMoisture,
        airTemperature = this.airTemperature,
        airHumidity = this.airHumidity
    )
}
