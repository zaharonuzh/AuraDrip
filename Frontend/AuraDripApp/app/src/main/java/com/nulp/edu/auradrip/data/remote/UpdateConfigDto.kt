package com.nulp.edu.auradrip.data.remote

import kotlinx.serialization.Serializable

@Serializable
data class UpdateConfigDto(
    val plantName: String? = null,
    val controlMode: Int? = null,
    val manualThreshold: Int? = null
)
