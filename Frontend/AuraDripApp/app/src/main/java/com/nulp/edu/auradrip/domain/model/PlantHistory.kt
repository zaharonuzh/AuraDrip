package com.nulp.edu.auradrip.domain.model

import java.time.Instant

/**
 * Основна модель історії для UI шару
 */
data class PlantHistory(
    val periodDays: Int,
    val averageMoisture: Double,
    val averageTemperature: Double,
    val averageAirHumidity: Double,
    val items: List<TelemetryPoint>
)

/**
 * Точка на графіку
 */
data class TelemetryPoint(
    val timestamp: Instant,
    val soilMoisture: Float,
    val airTemperature: Float,
    val airHumidity: Float
)