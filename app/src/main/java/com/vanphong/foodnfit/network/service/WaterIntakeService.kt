package com.vanphong.foodnfit.network.service

import com.vanphong.foodnfit.model.WaterIntakeResponse
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface WaterIntakeService {
    @GET("water/today")
    suspend fun getTodayWaterIntake(): Response<WaterIntakeResponse>
    @POST("water/update")
    suspend fun updateWaterCups(@Query("cups") cups: Int): Response<ResponseBody>
}