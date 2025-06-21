package com.vanphong.foodnfit.network.service

import com.vanphong.foodnfit.model.HourlyStepSummary
import com.vanphong.foodnfit.model.StepSummary
import com.vanphong.foodnfit.model.StepsTracking
import com.vanphong.foodnfit.model.StepsTrackingRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface StepsTrackingService {
    @POST("steps/create")
    suspend fun saveSteps(@Body request: StepsTrackingRequest?): Response<StepsTracking>

    @GET("steps/getAll")
    suspend fun getAll(): Response<List<StepsTracking>>

    @GET("steps/today-summary")
    suspend fun getTodaySummary(): Response<StepSummary>
    @GET("steps/hourly")
    suspend fun getHourlySummary(): Response<List<HourlyStepSummary>>
}