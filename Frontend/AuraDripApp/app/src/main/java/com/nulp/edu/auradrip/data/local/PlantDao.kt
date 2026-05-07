package com.nulp.edu.auradrip.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface PlantDao {
    @Query("SELECT * FROM plant_status WHERE plantId = :plantId LIMIT 1")
    fun getPlantStatus(plantId: Int): Flow<PlantStatusEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlantStatus(plantStatus: PlantStatusEntity)

    @Query("SELECT * FROM plant_history WHERE plantId = :plantId ORDER BY timestamp ASC")
    fun getHistoryForPlant(plantId: Int): Flow<List<PlantHistoryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHistory(history: List<PlantHistoryEntity>)

    @Query("DELETE FROM plant_history WHERE plantId = :plantId")
    suspend fun clearHistoryForPlant(plantId: Int)
}
