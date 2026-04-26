package com.nulp.edu.auradrip.data.repository

import com.nulp.edu.auradrip.data.local.PlantDao
import com.nulp.edu.auradrip.data.local.PlantStatusEntity
import com.nulp.edu.auradrip.data.remote.PlantApi
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
}
