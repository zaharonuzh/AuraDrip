package com.nulp.edu.auradrip.utils

import kotlinx.serialization.json.Json
import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.Instant
import java.time.ZoneOffset

class InstantSerializerTest {

    private val json = Json { ignoreUnknownKeys = true }

    @Test
    fun `deserialize standard ISO format with Z should work`() {
        val dateStr = "\"2026-05-07T21:00:00Z\""
        val result = json.decodeFromString(InstantSerializer, dateStr)

        assertEquals(Instant.parse("2026-05-07T21:00:00Z"), result)
    }

    @Test
    fun `deserialize problematic Render format without Z`() {
        val dateStr = "\"2026-04-23T17:25:14.8788425\""

        val result = json.decodeFromString(InstantSerializer, dateStr)

        // Перевіряємо, чи правильно серіалізатор додав UTC зміщення
        val expected = Instant.parse("2026-04-23T17:25:14.8788425Z")
        assertEquals(expected, result)
    }

    @Test
    fun `deserialize format with fewer fractional digits should work`() {
        // Перевірка гнучкості форматера ISO_LOCAL_DATE_TIME
        val dateStr = "\"2026-04-23T17:25:14.8\""
        val result = json.decodeFromString(InstantSerializer, dateStr)

        assertEquals(Instant.parse("2026-04-23T17:25:14.800Z"), result)
    }
}