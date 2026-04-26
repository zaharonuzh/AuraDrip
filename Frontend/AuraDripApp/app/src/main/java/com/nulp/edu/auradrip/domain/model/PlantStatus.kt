package com.nulp.edu.auradrip.domain.model

data class PlantStatus(
    val plantId: Int,
    val ageDays: Int,
    val currentMoisture: Double,
    val currentTemp: Double,
    val currentAirHum: Double,
    val lastUpdate: String?
)
