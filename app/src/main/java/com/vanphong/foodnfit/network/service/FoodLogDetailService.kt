package com.vanphong.foodnfit.network.service

import com.vanphong.foodnfit.model.FoodLogDetailBatchRequest
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.POST
import retrofit2.http.Path

interface FoodLogDetailService {
    @POST("food-log-detail/batch")
    suspend fun saveBatch(
        @Body request: FoodLogDetailBatchRequest
    ): Response<ResponseBody>

    @DELETE("food-log-detail/delete/{id}")
    suspend fun deleteById(
        @Path("id") id: Int
    ): Response<ResponseBody>
}