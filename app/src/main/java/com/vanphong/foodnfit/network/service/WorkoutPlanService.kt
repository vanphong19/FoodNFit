package com.vanphong.foodnfit.network.service

import com.vanphong.foodnfit.model.WeeklyExerciseSummaryResponse
import com.vanphong.foodnfit.model.WorkoutPlanByDate
import com.vanphong.foodnfit.model.WorkoutPlanCreateResponse
import com.vanphong.foodnfit.model.WorkoutPlanRequest
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface WorkoutPlanService {


    @DELETE("workout-plan/remove/{id}")
    suspend fun deleteWorkoutPlan(@Path("id") id: Int): Response<ResponseBody>


    @GET("workout-plan/weekly-summary")
    suspend fun getWeeklySummary(@Query("date") date: String): Response<WeeklyExerciseSummaryResponse>

    @GET("workout-plan/weekly-summary/current")
    suspend fun getCurrentWeeklySummary(): Response<WeeklyExerciseSummaryResponse>

    @POST("workout-plan/create")
    suspend fun createWorkoutPlan(@Body request: WorkoutPlanRequest): Response<WorkoutPlanCreateResponse>

    @GET("workout-plan/getByDate")
    suspend fun getWorkoutPlanByDate(@Query("date") day: String): Response<WorkoutPlanByDate>
}