package com.vanphong.foodnfit.network.service

import com.vanphong.foodnfit.Model.FcmTokenRequest
import com.vanphong.foodnfit.Model.StepsTracking
import com.vanphong.foodnfit.Model.StepsTrackingRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface StepsTrackingService {
    @POST("steps/create")
    suspend fun saveSteps(@Body request: StepsTrackingRequest?): Response<StepsTracking>

    @GET("steps/getAll")
    suspend fun getAll(): Response<List<StepsTracking>>
}