package com.nulp.edu.auradrip

import android.app.Application
import androidx.room.Room
import com.nulp.edu.auradrip.data.local.AppDatabase
import com.nulp.edu.auradrip.data.remote.PlantApi
import com.nulp.edu.auradrip.data.repository.PlantRepositoryImpl
import com.nulp.edu.auradrip.domain.repository.PlantRepository
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory

class AuraDripApplication : Application() {

    lateinit var plantRepository: PlantRepository

    override fun onCreate() {
        super.onCreate()
        
        val database = Room.databaseBuilder(
            this,
            AppDatabase::class.java,
            "auradrip_db"
        ).fallbackToDestructiveMigration().build()

        val json = Json { ignoreUnknownKeys = true }
        val contentType = "application/json".toMediaType()

        val retrofit = Retrofit.Builder()
            .baseUrl("https://auradrip-api.onrender.com") // Replace with real URL later
            .addConverterFactory(json.asConverterFactory(contentType))
            .build()

        val plantApi = retrofit.create(PlantApi::class.java)

        plantRepository = PlantRepositoryImpl(
            plantDao = database.plantDao(),
            plantApi = plantApi
        )
    }
}
