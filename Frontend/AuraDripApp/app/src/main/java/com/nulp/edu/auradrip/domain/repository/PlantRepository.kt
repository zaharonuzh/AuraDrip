package com.nulp.edu.auradrip.domain.repository

import com.nulp.edu.auradrip.domain.model.PlantStatus
import kotlinx.coroutines.flow.Flow

interface PlantRepository {
    fun getPlantStatus(plantId: Int): Flow<PlantStatus?>
    suspend fun fetchAndSavePlantStatus(plantId: Int): Result<Unit>
}
