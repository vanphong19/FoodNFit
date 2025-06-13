package com.vanphong.foodnfit.network.service

import retrofit2.Response
import retrofit2.http.GET

interface FeedbackService {
    @GET("feedback/count")
    suspend fun countFeedback(): Response<Long>
}