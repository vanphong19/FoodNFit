package com.vanphong.foodnfit.network.service

import com.vanphong.foodnfit.model.ExerciseRequest
import com.vanphong.foodnfit.model.Exercises
import com.vanphong.foodnfit.model.FoodItemResponse
import com.vanphong.foodnfit.model.PageResponse
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface ExerciseService {
    @POST("exercises/create")
    suspend fun createExercise(@Body request: ExerciseRequest): Response<Exercises>
    @GET("exercises/getAll")
    suspend fun getAllExercises(
        @Query("search") search: String? = "",
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 10,
        @Query("sortBy") sortBy: String = "id",
        @Query("sortDir") sortDir: String = "asc"
    ): Response<PageResponse<Exercises>>
    @GET("exercises/getById/{id}")
    suspend fun getById(@Path("id")id: Int): Response<Exercises>
    @GET("exercises/count")
    suspend fun countExercise():Response<Long>
    @GET("exercises/count-this-month")
    suspend fun countExerciseThisMonth(): Response<Long>
    @PUT("exercises/update/{id}")
    suspend fun update(@Path("id")id: Int, @Body request: ExerciseRequest): Response<Exercises>
    @DELETE("exercises/remove/{id}")
    suspend fun remove(@Path("id")id: Int): Response<ResponseBody>

}