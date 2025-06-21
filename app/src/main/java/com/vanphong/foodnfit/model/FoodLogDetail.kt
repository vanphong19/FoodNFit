package com.vanphong.foodnfit.model

data class FoodLogDetail (
    val id: Int,
    val logId: Int,
    val foodId: Int,
    val foodName: String,
    val servingSize: String,
    val calories: Double,
    val protein: Double,
    val carb: Double,
    val fat: Double
)

data class FoodLogDetailResponse(
    val id: Int,
    val foodId: Int,
    val foodNameEn: String,
    val foodNameVi: String,
    val servingSize: String,
    val calories: Double,
    val carbs: Double,
    val protein: Double,
    val fat: Double
)

data class FoodLogDetailBatchRequest(
    val logId: Int,
    val details: List<FoodLogDetailRequest>
)

data class FoodLogDetailRequest(
    val foodId: Int,
    val servingSize: String,
    val calories: Double,
    val carbs: Double,
    val protein: Double,
    val fat: Double
)