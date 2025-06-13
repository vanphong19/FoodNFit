package com.vanphong.foodnfit.network.service

import com.vanphong.foodnfit.Model.FoodItemResponse
import com.vanphong.foodnfit.Model.PageResponse
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface FoodItemService {
    @GET("food-item/count")
    suspend fun countFood(): Response<ResponseBody>
    @GET("food-item/count-this-month")
    suspend fun countFoodThisMonth(): Response<Long>
    @GET("food-item/getAll")
    suspend fun getAllFoodItems(
        @Query("search") search: String? = "",
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 10,
        @Query("sortBy") sortBy: String = "id",
        @Query("sortDir") sortDir: String = "asc"
    ): Response<PageResponse<FoodItemResponse>>
}