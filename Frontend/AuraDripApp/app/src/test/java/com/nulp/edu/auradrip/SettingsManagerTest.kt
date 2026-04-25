package com.nulp.edu.auradrip

import com.nulp.edu.auradrip.data.repository.SettingsRepository
import com.nulp.edu.auradrip.logic.SettingsManager
import com.nulp.edu.auradrip.logic.WorkMode
import io.mockk.mockk
import io.mockk.verify
import org.junit.Assert
import org.junit.Test

class SettingsManagerTest {
    private val repository = mockk<SettingsRepository>(relaxed = true)
    private val manager = SettingsManager(repository)

    @Test
    fun `should switch to CUSTOM mode when sync interval is manually updated`() {
        manager.setPresetMode(WorkMode.LONG_ABSENCE)
        Assert.assertEquals("System should switch to LONG_ABSENCE mode",WorkMode.LONG_ABSENCE, manager.getSettings().mode)

        val newInterval = 30
        manager.updateSyncInterval(newInterval)

        val resultSettings = manager.getSettings()

        Assert.assertEquals("System should switch to CUSTOM mode after sync update", WorkMode.CUSTOM, resultSettings.mode)
        Assert.assertEquals("Sync interval should change after update", newInterval, resultSettings.syncIntervalMinutes)

        verify { repository.saveSettings(match { it.mode == WorkMode.CUSTOM }) }
    }
}