package com.nulp.edu.auradrip.logic

import com.nulp.edu.auradrip.service.NotificationService

class WaterLevelMonitor(private val notificationService: NotificationService) {
    fun checkLevel(currentLevel: Int) {
        // Якщо рівень води менше 10%, надсилаємо критичне сповіщення
        if (currentLevel < 10) {
             notificationService.sendCriticalAlert()
        }
    }
}