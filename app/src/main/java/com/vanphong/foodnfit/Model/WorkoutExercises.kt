package com.vanphong.foodnfit.Model

import java.time.LocalDateTime

data class WorkoutExercises (
    val id: Int,
    val exerciseId: Int,
    val name: String,
    val imageUrl: String,
    val sets: Int?,
    val reps: Int?,
    val restTimeSeconds: Int?,
    val minute: Int?,
    val caloriesBurnt: Float,
    val startTime: LocalDateTime?,
    val endTime: LocalDateTime?,
    val planId: Int,
    val type: String,
    val isCompleted: Boolean
)