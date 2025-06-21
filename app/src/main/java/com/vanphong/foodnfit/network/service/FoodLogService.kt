package com.vanphong.foodnfit.network.service

import com.vanphong.foodnfit.model.FoodLogRequest
import com.vanphong.foodnfit.model.FoodLogResponse
import com.vanphong.foodnfit.model.WeeklyNutritionResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface FoodLogService {
    @POST("food-log/create")
    suspend fun createFoodLog(@Body request: FoodLogRequest): Response<FoodLogResponse>

    @PUT("food-log/update/{id}")
    suspend fun updateFoodLog(
        @Path("id") id: Int,
        @Body request: FoodLogRequest
    ): Response<FoodLogResponse>

    @DELETE("food-log/remove/{id}")
    suspend fun deleteFoodLog(@Path("id") id: Int): Response<String>

    @GET("food-log/getById/{id}")
    suspend fun getFoodLogById(@Path("id") id: Int): Response<FoodLogResponse>

    @GET("food-log/getByDate")
    suspend fun getFoodLogsByDate(@Query("day") day: String): Response<List<FoodLogResponse>>

    @GET("food-log/weekly-summary")
    suspend fun getWeeklySummary(@Query("date") date: String): Response<WeeklyNutritionResponse>
}