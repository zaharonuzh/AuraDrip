package com.nulp.edu.auradrip.logic

import kotlin.math.roundToInt

class MoistureCalculator {

    /**
     * Конвертує аналоговий сигнал у відсотки вологості.
     * Формула: ((Current - Dry) / (Wet - Dry)) * 100
     */
    fun calculatePercentage(current: Int, dryValue: Int, wetValue: Int): Int {
        // Запобігання діленню на нуль, якщо калібрування проведено некоректно
        if (dryValue == wetValue) return 0

        val percentage = (current.toDouble() - dryValue) / (wetValue - dryValue) * 100

        // Обмеження результату діапазоном 0-100%
        return percentage.roundToInt().coerceIn(0, 100)
    }
}