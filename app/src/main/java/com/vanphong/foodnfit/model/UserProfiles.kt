package com.vanphong.foodnfit.model

import java.time.LocalDate

data class UserProfiles (
    val id: String,
    val userId: String,
    val height: Float,
    val weight: Float,
    val tdee: Float,
    val goal: String,
    val date: LocalDate
)

data class UserProfileRequest(
    val height: Float,
    val weight: Float,
    val tdee: Float,
    val mealGoal: String,
    val exerciseGoal: String
)

data class UserProfileResponse(
    val id: String,
    val height: Float,
    val weight: Float,
    val tdee: Float,
    val bmi: Float,
    val mealGoal: String,
    val exerciseGoal: String,
    val createdAt: String,
    val birthday: String,
    val gender: Boolean,
    val avtUrl: String,
    val fullname: String,
    val email: String
)

data class MonthlyProfileResponse(
    val initialWeight: Float,
    val currentWeight: Float,
    val weightChange: Float,
    val currentBmi: Float,
    val bmiStatus: String,
    val weightHistory: List<WeightHistoryData>
)

data class WeightHistoryData(val date: String, val weight: Float)