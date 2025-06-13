package com.vanphong.foodnfit.Model

import java.math.BigDecimal

data class Exercises (
    val id: Int,
    val name: String,
    val description: String,
    val videoUrl: String,
    val imageUrl: String,
    val difficultyLevel: String,
    val muscleGroup: String,
    val equipmentRequired: String,
    val caloriesBurnt: Float,
    val minutes: Int?,
    val sets: Int?,
    val reps: Int?,
    val restTimeSeconds: Int?,
    val note: String,
    val type: String,
    val isActive: Boolean
)

data class ExerciseRequest(
    val exerciseName: String?,
    val description: String?,
    val videoUrl: String?,
    val imageUrl: String?,
    val difficultyLevel: String?,
    val muscleGroup: String?,
    val equipmentRequired: String?,
    val caloriesBurnt: Double?,
    val minutes: Int?,
    val sets: Int?,
    val reps: Int?,
    val restTimeSeconds: Int?,
    val note: String?,
    val exerciseType: String?,
    val active: Boolean?
)
