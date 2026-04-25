package com.nulp.edu.auradrip.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "plant_status")
data class PlantStatusEntity(
    @PrimaryKey
    val plantId: Int,
    val ageDays: Int,
    val currentMoisture: Double,
    val currentTemp: Double,
    val lastUpdate: String?
)
