package com.vanphong.foodnfit.repository

import com.vanphong.foodnfit.model.FoodItemRequest
import com.vanphong.foodnfit.model.FoodItemResponse
import com.vanphong.foodnfit.model.PageResponse
import com.vanphong.foodnfit.network.RetrofitClient
import com.vanphong.foodnfit.network.service.UploadResponse
import com.vanphong.foodnfit.util.safeCall
import com.vanphong.foodnfit.util.safeCallString
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import org.json.JSONObject
import retrofit2.Response
import java.io.File
import java.util.regex.Pattern

class FoodItemRepository {
    private val api = RetrofitClient.foodItemService
    private val uploadApi = RetrofitClient.uploadService
    suspend fun uploadImageFromFile(file: File): Response<UploadResponse> {
        val requestFile = file.asRequestBody("image/*".toMediaTypeOrNull())
        val body = MultipartBody.Part.createFormData("file", file.name, requestFile)
        return uploadApi.uploadImage(body)
    }

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

    suspend fun getFoods(search: String?, page: Int = 0): Result<PageResponse<FoodItemResponse>> {
        return safeCall { api.getAllFoodItems(search = search, page = page, size = 20) }
            .map { pageResponse ->
                pageResponse.copy(
                    content = processSingleServing(pageResponse.content)
                )
            }
    }

    private fun processSingleServing(items: List<FoodItemResponse>): List<FoodItemResponse> {
        val pattern = Pattern.compile("Serves (\\d+)") // Regex để tìm số người ăn

        return items.map { item ->
            val matcher = pattern.matcher(item.servingSizeEn)
            val servingCount = if (matcher.find()) {
                matcher.group(1)?.toIntOrNull() ?: 1
            } else {
                1
            }

            if (servingCount > 1) {
                // Tạo một đối tượng mới với các giá trị đã được chia
                item.copy(
                    calories = item.calories / servingCount,
                    protein = item.protein / servingCount,
                    carbs = item.carbs / servingCount,
                    fat = item.fat / servingCount,
                    servingSizeEn = "1 serving", // Cập nhật lại mô tả khẩu phần
                    servingSizeVi = "1 khẩu phần"
                )
            } else {
                item // Trả về item gốc nếu khẩu phần đã là 1
            }
        }
    }

    suspend fun getById(id: Int): Result<FoodItemResponse> = safeCall {
        api.getById(id)
    }

    suspend fun create(request: FoodItemRequest): Result<FoodItemResponse> = safeCall {
        api.create(request)
    }

    suspend fun update(id: Int, request: FoodItemRequest): Result<FoodItemResponse> = safeCall {
        api.update(id, request)
    }
    suspend fun remove(id: Int): Result<String> = safeCall {
        api.remove(id)
    }
}