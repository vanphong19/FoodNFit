package com.vanphong.foodnfit.model

import java.time.LocalDate
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

data class WeeklyNutritionResponse(
    val totalWeeklyCalories: Double,
    val mealSummaries: List<MealTypeSummaryDto>,
    val dailyCalorieChartData: List<DailyCalorieDataDto>
)

data class DailyCalorieDataDto(
    val date: String,
    val totalCalories: Double
)

data class MealTypeSummaryDto(
    val mealType: String,
    val totalCalories: Double,
    val percentage: Double
)

data class FoodLogRequest(
    val totalCalories: Double,
    val totalProtein: Double,
    val totalFat: Double,
    val totalCarbs: Double,
    val meal: String
)

data class FoodLogResponse(
    val id: Int,
    val userId: String,
    val totalCalories: Double,
    val totalProtein: Double,
    val totalFat: Double,
    val totalCarbs: Double,
    val date: String,
    val meal: String,
    val foodLogDetails: List<FoodLogDetailResponse>
)