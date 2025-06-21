package com.vanphong.foodnfit.network.service

import com.vanphong.foodnfit.model.Feedback
import com.vanphong.foodnfit.model.FeedbackRequest
import com.vanphong.foodnfit.model.FeedbackResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface FeedbackService {
    @GET("feedback/count")
    suspend fun countFeedback(): Response<Long>
    @POST("feedback/create")
    suspend fun createFeedback(@Body feedback: FeedbackRequest): Response<FeedbackResponse>
    @GET("feedback/getAll")
    suspend fun getAllFeedback(): Response<List<FeedbackResponse>>
}