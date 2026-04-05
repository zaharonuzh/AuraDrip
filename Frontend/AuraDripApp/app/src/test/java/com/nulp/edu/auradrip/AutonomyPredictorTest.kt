package com.nulp.edu.auradrip

import com.nulp.edu.auradrip.logic.AutonomyPredictor
import org.junit.Assert
import org.junit.Test

class AutonomyPredictorTest {

    private val predictor = AutonomyPredictor()

    @Test
    fun `should return 10 days for 500ml volume and 50ml daily consumption`() {
        val waterVolume = 500
        val consumptionPerDay = 50

        val result = predictor.calculateDaysRemaining(waterVolume, consumptionPerDay)

        Assert.assertEquals("Прогноз має бути рівно 10 днів для заданих параметрів",10, result)
    }

    @Test
    fun `should return 0 when daily consumption is zero`() {
        val result = predictor.calculateDaysRemaining(500, 0)

        Assert.assertEquals("При нульовій витраті прогноз має бути 0, щоб уникнути ділення на нуль",0, result)
    }

    @Test
    fun `should round down for non-integer results`() {
        // 500 / 60 = 8.33 -> має бути 8 повних днів
        val result = predictor.calculateDaysRemaining(500, 60)

        Assert.assertEquals("Система має повертати лише повну кількість днів (округлення вниз)",8, result)
    }
}