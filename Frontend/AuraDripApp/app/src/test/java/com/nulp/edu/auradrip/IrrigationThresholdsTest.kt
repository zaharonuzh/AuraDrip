package com.nulp.edu.auradrip

import com.nulp.edu.auradrip.logic.ThresholdValidators
import org.junit.Assert
import org.junit.Test

class IrrigationThresholdsTest {
    @Test
    fun startHumidityLevelIsLessThanStopLevel_shouldReturnTrue() {
        val startLevel = 25
        val stopLevel = 30
        val result = ThresholdValidators.irrigationThresholdValidate(startLevel, stopLevel)
        Assert.assertTrue(result)
    }

    @Test
    fun startHumidityLevelIsMoreOrEqualStopLevel_shouldReturnFalse() {
        val startLevel = 30
        val stopLevel = 25
        val result = ThresholdValidators.irrigationThresholdValidate(startLevel, stopLevel)
        Assert.assertFalse(result)
    }
}