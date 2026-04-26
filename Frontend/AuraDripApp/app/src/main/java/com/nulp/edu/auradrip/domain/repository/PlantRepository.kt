package com.nulp.edu.auradrip.domain.repository

import com.nulp.edu.auradrip.data.remote.ActionResponse
import com.nulp.edu.auradrip.data.remote.UpdateConfigDto
import com.nulp.edu.auradrip.domain.model.PlantConfig
import com.nulp.edu.auradrip.domain.model.PlantStatus
import kotlinx.coroutines.flow.Flow

interface PlantRepository {
    fun getPlantStatus(plantId: Int): Flow<PlantStatus?>
    suspend fun fetchAndSavePlantStatus(plantId: Int): Result<Unit>
    suspend fun forceWater(plantId: Int): Result<ActionResponse>
    
    suspend fun getPlantConfig(plantId: Int): Result<PlantConfig>
    suspend fun updatePlantConfig(plantId: Int, configDto: UpdateConfigDto): Result<ActionResponse>
}
