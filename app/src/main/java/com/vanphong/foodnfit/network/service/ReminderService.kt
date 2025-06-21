package com.vanphong.foodnfit.network.service

import com.vanphong.foodnfit.model.ReminderRequest
import com.vanphong.foodnfit.model.ReminderResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface ReminderService {
    @POST("reminder/create")
    suspend fun create(@Body request: ReminderRequest): Response<ReminderResponse>
    @GET("reminder/getAll")
    suspend fun getAll(): Response<List<ReminderResponse>>
    @GET("reminder/getById/{id}")
    suspend fun getById(@Path("id") id: Int): Response<ReminderResponse>
}