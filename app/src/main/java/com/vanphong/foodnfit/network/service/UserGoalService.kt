package com.vanphong.foodnfit.network.service

import com.vanphong.foodnfit.model.UserGoalResponse
import retrofit2.Response
import retrofit2.http.GET

interface UserGoalService {
    @GET("user-goal/getLatest")
    suspend fun getLatest(): Response<UserGoalResponse>
}