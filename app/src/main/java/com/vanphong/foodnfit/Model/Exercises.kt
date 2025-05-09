package com.vanphong.foodnfit.Model

data class Exercises (
    val id: Int,
    val name: String,
    val description: String,
    val videoUrl: String,
    val imageUrl: String,
    val difficultyLevel: String,
    val muscleGroup: String,
    val caloriesBurnt: Float,
    val minutes: Int?,
    val sets: Int?,
    val reps: Int?,
    val restTimeSeconds: Int?,
    val equipmentRequired: String,
    val note: String,
    val type: String,
    val isActive: Boolean
)