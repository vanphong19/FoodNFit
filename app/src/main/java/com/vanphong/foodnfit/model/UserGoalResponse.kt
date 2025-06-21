package com.vanphong.foodnfit.model

data class UserGoalResponse (
    val id: String,
    val targetCalories: Double,
    val targetCarbs: Double,
    val targetProtein: Double,
    val targetFat: Double,
    val caloriesBreakfast: Double,
    val caloriesLunch: Double,
    val caloriesDinner: Double,
    val caloriesSnack: Double,
    val createdAt: String
)