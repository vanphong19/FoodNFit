package com.vanphong.foodnfit.network.service

import com.vanphong.foodnfit.model.PageResponse
import com.vanphong.foodnfit.model.UserDailyStatsDto
import com.vanphong.foodnfit.model.UserRequest
import com.vanphong.foodnfit.model.UserResponse
import com.vanphong.foodnfit.model.UserResponseById
import com.vanphong.foodnfit.model.UserUpdateRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface UserService {
    @GET("user/count")
    suspend fun countUser(): Response<Long>
    @GET("user/count-this-month")
    suspend fun getCountUserThisMonth(): Response<Long>
    @GET("user/getAll")
    suspend fun getAllUsers(
        @Query("search") search: String? = "",
        @Query("gender") gender: Boolean? = null,
        @Query("block") block: Boolean? = null,
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 10,
        @Query("sortBy") sortBy: String = "id",
        @Query("sortDir") sortDir: String = "asc"
    ): Response<PageResponse<UserResponse>>

    @POST("user/create")
    suspend fun createUser(@Body request: UserRequest): Response<UserResponse>

    @GET("user/getById/{id}")
    suspend fun getUserById(@Path("id") id: String): Response<UserResponseById>

    @PUT("user/update/{id}")
    suspend fun updateUser(@Path("id") id: String, @Body request: UserUpdateRequest): Response<UserResponse>

    @DELETE("user/remove/{id}")
    suspend fun deleteUser(@Path("id") id: String): Response<UserResponse>

    @PUT("user/lock/{id}")
    suspend fun lockUser(@Path("id") id: String): Response<UserResponse>

    @GET("user/count-last-month")
    suspend fun countUsersLastMonth(): Response<Long>

    @GET("user/stats-today")
    suspend fun getUserStatsToday(): Response<UserDailyStatsDto>
}