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
}
