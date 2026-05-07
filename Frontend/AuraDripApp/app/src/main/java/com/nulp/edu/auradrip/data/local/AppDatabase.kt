package com.nulp.edu.auradrip.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [PlantStatusEntity::class, PlantHistoryEntity::class], // Додано PlantHistoryEntity
    version = 3, // Піднімаємо версію (була 2)
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun plantDao(): PlantDao
}
