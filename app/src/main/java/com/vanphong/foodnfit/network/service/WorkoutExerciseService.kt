package com.vanphong.foodnfit.network.service

import com.vanphong.foodnfit.model.WorkoutExerciseBatchRequest
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface WorkoutExerciseService {
    @POST("workout-exercise/batch")
    suspend fun saveAll(@Body request: WorkoutExerciseBatchRequest): Response<ResponseBody>

    @DELETE("workout-exercise/remove/{id}")
    suspend fun deleteExercise(@Path("id") id: Int): Response<ResponseBody>

    @PUT("workout-exercise/complete/{id}")
    suspend fun completeExercise(@Path("id") id: Int): Response<ResponseBody>
}