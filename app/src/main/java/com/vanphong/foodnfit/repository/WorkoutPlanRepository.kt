package com.vanphong.foodnfit.repository

import com.vanphong.foodnfit.model.FoodLogResponse
import com.vanphong.foodnfit.model.WeeklyExerciseSummaryResponse
import com.vanphong.foodnfit.model.WorkoutPlanByDate
import com.vanphong.foodnfit.model.WorkoutPlanCreateResponse
import com.vanphong.foodnfit.model.WorkoutPlanRequest
import com.vanphong.foodnfit.network.RetrofitClient
import com.vanphong.foodnfit.util.safeCall
import com.vanphong.foodnfit.util.safeCallString
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class WorkoutPlanRepository {
    private val api = RetrofitClient.workoutPlanService

    suspend fun getWeeklySummary(date: String): Result<WeeklyExerciseSummaryResponse> = safeCall {
        api.getWeeklySummary(date)
    }

    suspend fun deleteWorkoutPlan(id: Int): Result<String> = safeCallString {
        api.deleteWorkoutPlan(id)
    }

    suspend fun createWorkoutPlan(request: WorkoutPlanRequest): Result<WorkoutPlanCreateResponse> = safeCall {
        api.createWorkoutPlan(request)
    }
    suspend fun getWorkoutPlanByDate(date: LocalDate): Result<WorkoutPlanByDate> = safeCall {
        val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
        val dateString = date.format(formatter)
        api.getWorkoutPlanByDate(dateString)
    }
}