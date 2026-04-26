package com.nulp.edu.auradrip

import com.nulp.edu.auradrip.logic.WaterLevelMonitor
import com.nulp.edu.auradrip.service.NotificationService
import io.mockk.mockk
import io.mockk.verify
import org.junit.Assert
import org.junit.Test

class NotificationTriggerTest {
    private val notificationService = mockk<NotificationService>(relaxed = true)
    private val monitor = WaterLevelMonitor(notificationService)

    @Test
    fun testCriticalAlertTriggeredWhenLevelIsNine() {
        // Встановлюємо рівень води 9% (що менше порогу 10%)
        val waterLevel = 9

        // Система виконує перевірку показників датчика
        monitor.checkLevel(waterLevel)

        // Перевіряємо, що метод сповіщення був викликаний рівно 1 раз
        verify(exactly = 1) { notificationService.sendCriticalAlert() }

        Assert.assertTrue(
            "Система мала ініціювати критичне сповіщення, оскільки рівень води ($waterLevel%) нижче за 10%",
            waterLevel < 10
        )
    }

    @Test
    fun testAlertNotTriggeredWhenLevelIsEleven() {
        // Рівень води 11% (вище порогу)
        val waterLevel = 11

        monitor.checkLevel(waterLevel)

        // Перевіряємо, що метод НЕ був викликаний
        verify(exactly = 0) { notificationService.sendCriticalAlert() }

        Assert.assertEquals(
            "При рівні 11% сповіщення не повинно надсилатися",
            11,
            waterLevel
        )
    }
}