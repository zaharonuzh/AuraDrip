package com.nulp.edu.auradrip.data.repository

import com.nulp.edu.auradrip.logic.DeviceSettings
import com.nulp.edu.auradrip.logic.WorkMode

class SettingsRepositoryImpl: SettingsRepository {

    private var currentSettings: DeviceSettings = DeviceSettings(
        mode = WorkMode.BEGINNER,
        syncIntervalMinutes = 60,
        showTips = true
    )

    override fun saveSettings(settings: DeviceSettings) {
        currentSettings = settings
    }
}