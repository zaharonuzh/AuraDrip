package com.nulp.edu.auradrip.logic

import kotlin.math.floor

class AutonomyPredictor {
    /**
     * Розраховує кількість повних днів до вичерпання води (FR-205).
     * @param currentVolumeMl Поточний об'єм води в мілілітрах.
     * @param dailyConsumptionMl Середня витрата води за добу.
     * @return Кількість днів або 0, якщо дані некоректні.
     */
    fun calculateDaysRemaining(currentVolumeMl: Int, dailyConsumptionMl: Int): Int {
        if (dailyConsumptionMl <= 0 || currentVolumeMl <= 0) return 0

        // Математичний розрахунок згідно зі специфікацією
        val days = currentVolumeMl.toDouble() / dailyConsumptionMl.toDouble()

        return floor(days).toInt()
    }
}