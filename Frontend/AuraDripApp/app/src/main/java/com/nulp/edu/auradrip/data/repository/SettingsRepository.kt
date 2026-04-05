package com.nulp.edu.auradrip.data.repository

import com.nulp.edu.auradrip.logic.DeviceSettings

interface SettingsRepository {
    fun saveSettings(settings: DeviceSettings)
}