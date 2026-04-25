package com.nulp.edu.auradrip.data.repository

class SensorRepositoryImpl: SensorRepository {

    override fun getWaterLevel(): Int {
        return 5
    }

    override fun getSoilMoisture(): Int {
        return 10
    }
}