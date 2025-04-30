package com.vanphong.foodnfit.Model

data class Exercises (
    val id: Int,
    val name: String,
    val description: String,
    val videoUrl: String,
    val difficultyLevel: String,
    val muscleGroup: String,
    val equipmentRequired: String,
    val type: String,
    val isActive: Boolean
)