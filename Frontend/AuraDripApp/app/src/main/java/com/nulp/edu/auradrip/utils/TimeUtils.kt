package com.nulp.edu.auradrip.utils

import java.time.Instant
import java.time.Duration

fun String?.toTimeAgo(): String {
    if (this == null) return "Очікування даних від ESP32..."
    
    return try {
        val instant = Instant.parse(this)
        val now = Instant.now()
        val duration = Duration.between(instant, now)
        
        val minutes = duration.toMinutes()
        val hours = duration.toHours()
        val days = duration.toDays()
        
        when {
            minutes < 1 -> "Щойно"
            minutes < 60 -> "$minutes хвилин тому"
            hours < 24 -> "$hours годин тому"
            else -> "$days днів тому"
        }
    } catch (e: Exception) {
        "Невідомий час"
    }
}
