package com.vanphong.foodnfit.network.service

import com.vanphong.foodnfit.Model.ExerciseRequest
import com.vanphong.foodnfit.Model.Exercises
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface ExerciseService {
    @POST("exercises/create")
    suspend fun createExercise(@Body request: ExerciseRequest): Response<Exercises>
    @GET("exercises/getAll")
    suspend fun getAll(): Response<List<Exercises>>
    @GET("exercises/getById/{id}")
    suspend fun getById(@Path("id")id: Int): Response<Exercises>
    @GET("exercises/count")
    suspend fun countExercise():Response<Long>
    @GET("exercises/count-this-month")
    suspend fun countExerciseThisMonth(): Response<Long>
}