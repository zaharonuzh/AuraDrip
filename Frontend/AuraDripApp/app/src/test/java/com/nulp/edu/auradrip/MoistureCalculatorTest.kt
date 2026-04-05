package com.nulp.edu.auradrip

import com.nulp.edu.auradrip.logic.MoistureCalculator
import org.junit.Assert
import org.junit.Test

class MoistureCalculatorTest {

    private val calculator = MoistureCalculator()

    @Test
    fun `should return 50 percent for mid-range signal`() {
        val dry = 3000
        val wet = 1000
        val current = 2000

        val result = calculator.calculatePercentage(current, dry, wet)

        Assert.assertEquals( "Для сигналу 2000 між 3000(сухо) та 1000(мокро) результат має бути 50%",50, result)
    }

    @Test
    fun `should return 100 percent when current equals wet value`() {
        val result = calculator.calculatePercentage(1000, 3000, 1000)
        Assert.assertEquals(100, result)
    }

    @Test
    fun `should return 0 percent when current equals dry value`() {
        val result = calculator.calculatePercentage(3000, 3000, 1000)
        Assert.assertEquals(0, result)
    }

    @Test
    fun `should clump result within 0 to 100 range`() {
        // Сигнал 3500 має повертати 0%, а не від'ємне число
        val resultAbove = calculator.calculatePercentage(3500, 3000, 1000)
        Assert.assertEquals(0, resultAbove)

        // Сигнал 500 має повертати 100%
        val resultBelow = calculator.calculatePercentage(500, 3000, 1000)
        Assert.assertEquals(100, resultBelow)
    }
}