package com.vanphong.foodnfit.model

import java.time.LocalDate
import java.util.UUID

data class WorkoutPlan (
    val id: Int,
    val userId: UUID,
    val exerciseCount: Int,
    val date: LocalDate,
    val caloriesBurnt: Float
)

data class WeeklyExerciseSummaryResponse(
    val totalCaloriesBurnt: Double,
    val totalTrainingSessions: Int,
    val totalSteps: Int,
    val averageCaloriesPerSession: Double,
    val bestDay: String,
    val dailyCalorieChartData: List<DailyCaloriesExerciseDto>,
    val favoriteExercises: List<FavoriteExerciseDto>
)

data class FavoriteExerciseDto(
    val exerciseName: String,
    val totalSessions: Int,
    val totalCalories: Double,
    val muscleGroup: String
)

data class DailyCaloriesExerciseDto(
    val date: String,
    val totalCalories: Double,
    val exerciseCount: Int
)

data class WorkoutPlanRequest(val exerciseCount: Int, val totalCaloriesBurnt: Double)
data class WorkoutPlanCreateResponse(
    val id: Int,
    val userId: String,
    val exerciseCount: Int,
    val planDate: String,
    val totalCaloriesBurnt: Double,
)

data class WorkoutPlanByDate(
    val id: Int,
    val userId: String,
    val exerciseCount: Int,
    val planDate: String,
    val totalCaloriesBurnt: Double,
    val exerciseResponses: List<WorkoutExerciseResponse>
)

data class WorkoutExerciseResponse(
    val id: Int,
    val exerciseName: String,
    val exerciseId: Int,
    val planId: Int,
    val sets: Int,
    val reps: Int,
    val restTimeSecond: Int,
    val caloriesBurnt: Int,
    val minutes: Int,
    val isCompleted: Boolean
)