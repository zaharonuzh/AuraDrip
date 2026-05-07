package com.nulp.edu.auradrip.data.mapper

import com.nulp.edu.auradrip.data.remote.PlantHistoryItemDto
import com.nulp.edu.auradrip.domain.model.TelemetryPoint
import java.time.Instant
import org.junit.Assert.assertEquals
import org.junit.Test

class PlantMapperTest {

    fun PlantHistoryItemDto.toDomain(): TelemetryPoint {
        return TelemetryPoint(
            timestamp = this.timestamp,
            soilMoisture = this.soilMoisture.toFloat(),
            airTemperature = this.airTemperature.toFloat(),
            airHumidity = this.airHumidity.toFloat()
        )
    }

    @Test
    fun `PlantHistoryItemDto toDomain maps correctly`() {
        // Given
        val timestamp = Instant.now()
        val dto = PlantHistoryItemDto(
            timestamp = timestamp,
            soilMoisture = 45.5,
            airTemperature = 22.1,
            airHumidity = 60.0
        )

        // When
        val domain = dto.toDomain()

        // Then
        assertEquals(timestamp, domain.timestamp)
        assertEquals(45.5f, domain.soilMoisture, 0.001f) // Перевірка Float
        assertEquals(22.1f, domain.airTemperature, 0.001f)
        assertEquals(60.0f, domain.airHumidity, 0.001f)
    }
}