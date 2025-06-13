package com.vanphong.foodnfit.repository

import com.vanphong.foodnfit.network.RetrofitClient
import com.vanphong.foodnfit.util.safeCall

class FeedbackRepository {
    private val api = RetrofitClient.feedbackService

    suspend fun count(): Result<Long> = safeCall {
        api.countFeedback()
    }
}