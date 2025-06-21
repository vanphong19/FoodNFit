package com.vanphong.foodnfit.repository

import com.vanphong.foodnfit.model.FoodLogRequest
import com.vanphong.foodnfit.model.FoodLogResponse
import com.vanphong.foodnfit.model.WeeklyNutritionResponse
import com.vanphong.foodnfit.network.RetrofitClient
import com.vanphong.foodnfit.util.safeCall
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class FoodLogRepository {
    private val api = RetrofitClient.foodLogService

    suspend fun getWeeklySummary(date: String): Result<WeeklyNutritionResponse> = safeCall {
        api.getWeeklySummary(date)
    }

    suspend fun getFoodLogsByDate(date: LocalDate): Result<List<FoodLogResponse>> = safeCall {
        val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
        val dateString = date.format(formatter)
        api.getFoodLogsByDate(dateString)
    }

    suspend fun getFoodLogById(id: Int): Result<FoodLogResponse> = safeCall {
        api.getFoodLogById(id)
    }

    suspend fun create(request: FoodLogRequest): Result<FoodLogResponse> = safeCall {
        api.createFoodLog(request)
    }
}