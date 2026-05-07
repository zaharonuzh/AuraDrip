package com.nulp.edu.auradrip.data.remote

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import java.time.Instant
import com.nulp.edu.auradrip.utils.InstantSerializer
import kotlinx.coroutines.flow.emitAll

@Serializable
data class PlantHistoryResponse(
    val periodDays: Int,
    val totalRecords: Int,
    val statistics: PlantStatisticsDto,
    val history: List<PlantHistoryItemDto>
)

@Serializable
data class PlantStatisticsDto(
    val averageMoisture: Double,
    val averageTemperature: Double,
    val averageAirHumidity: Double
)

@Serializable
data class PlantHistoryItemDto(
    @Serializable(with = InstantSerializer::class)
    val timestamp: Instant,
    val soilMoisture: Double,
    val airTemperature: Double,
    val airHumidity: Double
)