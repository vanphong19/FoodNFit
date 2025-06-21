package com.vanphong.foodnfit.repository

import com.vanphong.foodnfit.model.FoodLogDetailBatchRequest
import com.vanphong.foodnfit.network.RetrofitClient
import com.vanphong.foodnfit.util.safeCallString

class FoodLogDetailRepository {
    private val api = RetrofitClient.foodLogDetailService
    suspend fun saveBatch(request: FoodLogDetailBatchRequest): Result<String> = safeCallString {
        api.saveBatch(request)
    }

    suspend fun deleteById(id: Int): Result<String> = safeCallString {
        api.deleteById(id)
    }
}