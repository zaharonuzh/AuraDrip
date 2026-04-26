package com.nulp.edu.auradrip.data.remote

import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path

interface PlantApi {
    @GET("/api/app/plants/{plantId}/status")
    suspend fun getPlantStatus(@Path("plantId") plantId: Int): PlantStatusResponse

    @POST("/api/app/plants/{plantId}/force-water")
    suspend fun forceWater(@Path("plantId") plantId: Int): ActionResponse

    @GET("/api/app/plants/{plantId}/config")
    suspend fun getPlantConfig(@Path("plantId") plantId: Int): PlantConfigResponse

    @PATCH("/api/app/plants/{plantId}/config")
    suspend fun updatePlantConfig(
        @Path("plantId") plantId: Int,
        @Body config: UpdateConfigDto
    ): ActionResponse
}
