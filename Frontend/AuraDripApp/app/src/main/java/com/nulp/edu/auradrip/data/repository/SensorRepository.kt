package com.nulp.edu.auradrip.data.repository

interface SensorRepository {

    fun getWaterLevel(): Int

    fun getSoilMoisture(): Int

}