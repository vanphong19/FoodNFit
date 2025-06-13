package com.vanphong.foodnfit.repository

import com.vanphong.foodnfit.Model.FoodItemResponse
import com.vanphong.foodnfit.Model.PageResponse
import com.vanphong.foodnfit.Model.UserResponse
import com.vanphong.foodnfit.network.RetrofitClient
import com.vanphong.foodnfit.util.safeCall
import com.vanphong.foodnfit.util.safeCallString
import org.json.JSONObject

class FoodItemRepository {
    private val api = RetrofitClient.foodItemService

    suspend fun getFoodCount(): Result<Long> {
        return safeCallString {
            api.countFood()
        }.mapCatching { jsonString ->
            val json = JSONObject(jsonString)
            json.getLong("total_foods")  // hoặc getInt nếu là số nguyên nhỏ
        }
    }

    suspend fun countFoodThisMonth(): Result<Long> = safeCall {
        api.countFoodThisMonth()
    }

    suspend fun getAllFoods(
        search: String? = "",
        page: Int = 0,
        size: Int = 10,
        sortBy: String = "id",
        sortDir: String = "asc"
    ): Result<PageResponse<FoodItemResponse>> {
        return safeCall {
            api.getAllFoodItems(search, page, size, sortBy, sortDir)
        }
    }
}