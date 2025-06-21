package com.vanphong.foodnfit.repository

import com.vanphong.foodnfit.model.UserGoalResponse
import com.vanphong.foodnfit.network.RetrofitClient
import com.vanphong.foodnfit.util.safeCall

class UserGoalRepository {
    private val api = RetrofitClient.userGoalService

    suspend fun getLatest(): Result<UserGoalResponse> = safeCall {
        api.getLatest()
    }
}