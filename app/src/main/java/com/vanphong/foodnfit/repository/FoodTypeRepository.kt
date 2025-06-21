package com.vanphong.foodnfit.repository

import com.vanphong.foodnfit.model.FoodType
import com.vanphong.foodnfit.network.RetrofitClient
import com.vanphong.foodnfit.util.safeCall

class FoodTypeRepository {
    private val api = RetrofitClient.foodTypeService
    suspend fun getAll(): Result<List<FoodType>> = safeCall {
        api.getAll()
    }
}