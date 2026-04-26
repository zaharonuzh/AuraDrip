package com.nulp.edu.auradrip.data.repository

import com.nulp.edu.auradrip.data.local.PlantDao
import com.nulp.edu.auradrip.data.local.PlantStatusEntity
import com.nulp.edu.auradrip.data.remote.ActionResponse
import com.nulp.edu.auradrip.data.remote.PlantApi
import com.nulp.edu.auradrip.data.remote.UpdateConfigDto
import com.nulp.edu.auradrip.domain.model.PlantConfig
import com.nulp.edu.auradrip.domain.model.PlantStatus
import com.nulp.edu.auradrip.domain.repository.PlantRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

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
                    manualThreshold = response.manualThreshold
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
}
