package com.nulp.edu.auradrip.data.remote

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PlantStatusResponse(
    @SerialName("ageDays")
    val ageDays: Int,
    @SerialName("currentMoisture")
    val currentMoisture: Double,
    @SerialName("currentTemp")
    val currentTemp: Double,
    @SerialName("lastUpdate")
    val lastUpdate: String? = null
)
