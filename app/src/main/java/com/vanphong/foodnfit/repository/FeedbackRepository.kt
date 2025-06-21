package com.vanphong.foodnfit.repository

import com.vanphong.foodnfit.model.FeedbackRequest
import com.vanphong.foodnfit.model.FeedbackResponse
import com.vanphong.foodnfit.network.RetrofitClient
import com.vanphong.foodnfit.network.service.UploadResponse
import com.vanphong.foodnfit.util.safeCall
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import retrofit2.Response
import java.io.File

class FeedbackRepository {
    private val api = RetrofitClient.feedbackService

    suspend fun count(): Result<Long> = safeCall {
        api.countFeedback()
    }

    suspend fun createFeedback(request: FeedbackRequest): Result<FeedbackResponse> = safeCall {
        api.createFeedback(request)
    }
    suspend fun getAllFeedback(): Result<List<FeedbackResponse>> = safeCall {
        api.getAllFeedback()
    }

    private val uploadApi = RetrofitClient.uploadService
    suspend fun uploadImageFromFile(file: File): Response<UploadResponse> {
        val requestFile = file.asRequestBody("image/*".toMediaTypeOrNull())
        val body = MultipartBody.Part.createFormData("file", file.name, requestFile)
        return uploadApi.uploadImage(body)
    }
}