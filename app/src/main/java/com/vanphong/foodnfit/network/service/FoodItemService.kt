package com.vanphong.foodnfit.network.service

import com.vanphong.foodnfit.model.FoodItemRequest
import com.vanphong.foodnfit.model.FoodItemResponse
import com.vanphong.foodnfit.model.PageResponse
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
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
    @GET("food-item/getById/{id}")
    suspend fun getById(@Path("id") id: Int): Response<FoodItemResponse>
    @PUT("food-item/update/{id}")
    suspend fun update(@Path("id")id: Int, @Body request: FoodItemRequest): Response<FoodItemResponse>
    @DELETE("food-item/remove/{id}")
    suspend fun remove(@Path("id")id: Int): Response<String>
    @POST("food-item/create")
    suspend fun create(@Body request: FoodItemRequest): Response<FoodItemResponse>
}