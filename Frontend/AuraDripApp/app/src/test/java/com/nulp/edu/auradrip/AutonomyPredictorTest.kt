package com.nulp.edu.auradrip

import com.nulp.edu.auradrip.logic.AutonomyPredictor
import org.junit.Assert.assertEquals
import org.junit.Test

class AutonomyPredictorTest {

    private val predictor = AutonomyPredictor()

    @Test
    fun calculateDaysRemaining_normalConditions_returnsCorrectDays() {
        // Базовий сценарій
        val result = predictor.calculateDaysRemaining(1200, 200)
        assertEquals("Розрахунок для 1200мл та 200мл/день має бути 6 днів", 6, result)
    }

    @Test
    fun calculateDaysRemaining_zeroConsumption_returnsZeroToAvoidCrash() {
        // Перевірка ділення на нуль
        val result = predictor.calculateDaysRemaining(850, 0)
        assertEquals("При витраті 0 має повертатися 0 (захист від крашу)", 0, result)
    }

    @Test
    fun calculateDaysRemaining_fractionalResult_roundsDown() {
        // Перевірка округлення
        val result = predictor.calculateDaysRemaining(1000, 150)
        assertEquals("1000 / 150 = 6.66, має округлюватись до 6 повних днів", 6, result)
    }

    @Test
    fun calculateDaysRemaining_consumptionGreaterThanVolume_returnsZero() {
        // Витрата більша за наявний об'єм
        val result = predictor.calculateDaysRemaining(100, 500)
        assertEquals("Якщо денна витрата більша за залишок, має вистачити на 0 повних днів", 0, result)
    }

    @Test
    fun calculateDaysRemaining_negativeValues_handledAsZero() {
        // Перевірка некоректних (від'ємних) вхідних даних
        val result = predictor.calculateDaysRemaining(-500, 50)
        assertEquals("Від'ємний об'єм має оброблятися як 0 днів", 0, result)
    }
}