package com.nulp.edu.auradrip.logic

import com.nulp.edu.auradrip.data.repository.SensorRepository
import com.nulp.edu.auradrip.data.repository.SensorRepositoryImpl

enum class PumpAction{
    PUMP_OFF,
    PUMP_ON
}

class PumpController(private val repository: SensorRepository) {

    fun determinePumpAction(threshold: Int): PumpAction {
        val waterLevel = repository.getWaterLevel() // Виклик через інтерфейс
        val soilMoisture = repository.getSoilMoisture()

        // 3. Реалізація бізнес-логіки з SRS (FR-311: захист від сухого ходу)
        if (waterLevel < 10) {
            return PumpAction.PUMP_OFF
        }

        // Логіка автоматичного поливу (FR-303)
        return if (soilMoisture < threshold) {
            PumpAction.PUMP_ON
        } else {
            PumpAction.PUMP_OFF
        }
    }
}