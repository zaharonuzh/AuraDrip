package com.nulp.edu.auradrip.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "plant_history")
data class PlantHistoryEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val plantId: Int,
    val timestamp: Long, // Зберігаємо як мілісекунди (Long) для Room
    val soilMoisture: Float,
    val airTemperature: Float,
    val airHumidity: Float
)