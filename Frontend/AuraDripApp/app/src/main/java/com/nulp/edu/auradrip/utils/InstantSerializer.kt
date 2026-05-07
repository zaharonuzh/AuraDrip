package com.nulp.edu.auradrip.utils // або твій пакет для утиліт

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

object InstantSerializer : KSerializer<Instant> {
    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("Instant", PrimitiveKind.STRING)

    // Форматер, який гнучко обробляє різну кількість цифр у наносекундах
    private val formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME

    override fun serialize(encoder: Encoder, value: Instant) {
        encoder.encodeString(value.toString())
    }

    override fun deserialize(decoder: Decoder): Instant {
        val string = decoder.decodeString()
        return try {
            // Спробуємо стандартний парсинг (якщо є 'Z')
            Instant.parse(string)
        } catch (e: Exception) {
            // Якщо сервер надсилає без 'Z', парсимо як LocalDateTime і додаємо зміщення UTC
            LocalDateTime.parse(string, formatter).toInstant(ZoneOffset.UTC)
        }
    }
}