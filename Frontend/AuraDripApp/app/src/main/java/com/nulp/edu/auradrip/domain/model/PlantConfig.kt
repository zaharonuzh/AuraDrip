package com.nulp.edu.auradrip.domain.model

data class PlantConfig(
    val plantId: Int,
    val plantName: String,
    val controlMode: Int,
    val manualThreshold: Int?
)
