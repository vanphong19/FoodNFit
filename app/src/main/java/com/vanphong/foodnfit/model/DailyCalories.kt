package com.vanphong.foodnfit.model

import java.time.LocalDate

data class DailyCalories (
    val todayMeal: List<FoodLog>
){
    val date: LocalDate
        get() = todayMeal[0].date
    val totalCalorie: Float
        get() = todayMeal.sumOf { it.totalCalories }.toFloat()
}