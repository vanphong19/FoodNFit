package com.vanphong.foodnfit.repository

import com.vanphong.foodnfit.model.WaterIntakeResponse
import com.vanphong.foodnfit.network.RetrofitClient
import com.vanphong.foodnfit.util.safeCall
import com.vanphong.foodnfit.util.safeCallString

class WaterIntakeRepository {
    private val api = RetrofitClient.waterIntakeService
    suspend fun getTodayWaterIntake(): Result<WaterIntakeResponse> = safeCall {
        api.getTodayWaterIntake()
    }

    suspend fun updateWaterCups(cups: Int): Result<String> = safeCallString {
        api.updateWaterCups(cups)
    }
}