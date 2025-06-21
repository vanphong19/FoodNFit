package com.vanphong.foodnfit.network.service

import com.vanphong.foodnfit.model.MonthlyProfileResponse
import com.vanphong.foodnfit.model.UserProfileRequest
import com.vanphong.foodnfit.model.UserProfileResponse
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Query

interface UserProfileService {
    @POST("user-profile/create")
    suspend fun create(@Body request: UserProfileRequest): Response<ResponseBody>
    @GET("user-profile/getLatest")
    suspend fun getLatest(): Response<UserProfileResponse>

    @GET("user-profile/monthly-stats")
    suspend fun getMonthlyStatistics(
        @Query("year") year: Int,
        @Query("month") month: Int
    ): Response<MonthlyProfileResponse>
}