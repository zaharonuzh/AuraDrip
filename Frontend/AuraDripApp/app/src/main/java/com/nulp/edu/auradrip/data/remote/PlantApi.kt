package com.nulp.edu.auradrip.data.remote

import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface PlantApi {
    @GET("/api/app/plants/{plantId}/status")
    suspend fun getPlantStatus(@Path("plantId") plantId: Int): PlantStatusResponse

    @POST("/api/app/plants/{plantId}/force-water")
    suspend fun forceWater(@Path("plantId") plantId: Int): ActionResponse
}
