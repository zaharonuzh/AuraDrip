package com.nulp.edu.auradrip

import com.nulp.edu.auradrip.data.repository.SensorRepository
import com.nulp.edu.auradrip.logic.PumpAction
import com.nulp.edu.auradrip.logic.PumpController
import io.mockk.every
import io.mockk.mockk
import org.junit.Assert
import org.junit.Test

class IrrigationServiceTest {

    private val sensorRepository = mockk<SensorRepository>()
    private val controller = PumpController(sensorRepository)

    @Test
    fun `should block pump when water level is critically low`() {
        every { sensorRepository.getWaterLevel() } returns 5
        every { sensorRepository.getSoilMoisture() } returns 10

        val action = controller.determinePumpAction(threshold = 20)

        Assert.assertEquals(PumpAction.PUMP_OFF, action)
    }
}