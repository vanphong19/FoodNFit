package com.vanphong.foodnfit.Model

data class FoodLogDetail (
    val id: Int,
    val logId: Int,
    val foodId: Int,
    val servingSize: String,
    val calories: Double,
    val protein: Double,
    val carb: Double,
    val fat: Double
)