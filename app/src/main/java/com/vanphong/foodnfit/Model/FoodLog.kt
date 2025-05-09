package com.vanphong.foodnfit.Model

import java.time.LocalDate
import java.util.Date
import java.util.UUID

data class FoodLog (
    val id: Int,
    val userId: UUID,
    val totalCalories: Double,
    val date: LocalDate,
    val totalProtein: Double?,
    val totalFat: Double?,
    val totalCarb: Double?,
    val meal: String,
    val details: List<FoodLogDetail>
)