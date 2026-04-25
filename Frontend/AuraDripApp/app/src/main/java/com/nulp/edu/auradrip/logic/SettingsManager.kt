package com.nulp.edu.auradrip.logic

import com.nulp.edu.auradrip.data.repository.SettingsRepository

enum class WorkMode {
    BEGINNER, EXPERT, LONG_ABSENCE, CUSTOM
}

data class DeviceSettings(
    val mode: WorkMode,
    val syncIntervalMinutes: Int,
    val showTips: Boolean
)

class SettingsManager(private val repository: SettingsRepository) {

    private var currentSettings = DeviceSettings(WorkMode.BEGINNER, 60, true)

    fun getSettings() = currentSettings

    // Функція встановлення пресету (FR-301, FR-305)
    fun setPresetMode(mode: WorkMode) {
        currentSettings = when(mode) {
            WorkMode.LONG_ABSENCE -> DeviceSettings(mode, 720, false) // 12 годин
            WorkMode.BEGINNER -> DeviceSettings(mode, 60, true)
            WorkMode.EXPERT -> DeviceSettings(mode, 15, false)
            WorkMode.CUSTOM -> currentSettings // Не змінюємо параметри
        }
    }

    // Функція зміни параметра (FR-309)
    fun updateSyncInterval(newInterval: Int) {
        // Якщо ми змінюємо параметр у будь-якому режимі — переходимо в CUSTOM
        currentSettings = currentSettings.copy(
            mode = WorkMode.CUSTOM,
            syncIntervalMinutes = newInterval
        )
        repository.saveSettings(currentSettings)
    }
}