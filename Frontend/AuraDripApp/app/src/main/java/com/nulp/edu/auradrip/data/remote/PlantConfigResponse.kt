package com.nulp.edu.auradrip.data.remote

import kotlinx.serialization.Serializable

@Serializable
data class PlantConfigResponse(
    val plantId: Int,
    val plantName: String,
    val controlMode: Int,
    val manualThreshold: Int? = null
)
