package com.vanphong.foodnfit.network.service

import com.vanphong.foodnfit.model.FoodType
import retrofit2.Response
import retrofit2.http.GET

interface FoodTypeService {
    @GET("food-type/getAll")
    suspend fun getAll(): Response<List<FoodType>>
}