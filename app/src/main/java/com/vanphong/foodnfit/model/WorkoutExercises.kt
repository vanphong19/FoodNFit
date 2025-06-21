package com.vanphong.foodnfit.model

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

data class WorkoutExerciseBatchRequest(
    val workoutId: Int,
    val workoutExerciseRequests: List<WorkoutExerciseRequest>
)

data class WorkoutExerciseRequest(
    val exerciseId: Int,
    val sets: Int?,
    val reps: Int?,
    val restTimeSecond: Int?,
    val caloriesBurnt: Double,
    val minutes: Int?
)

data class SelectableExercise(
    val exercise: Exercises,
    var isSelected: Boolean = false
)