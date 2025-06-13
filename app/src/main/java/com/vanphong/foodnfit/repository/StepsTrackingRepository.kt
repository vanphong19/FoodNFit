package com.vanphong.foodnfit.repository

import com.vanphong.foodnfit.Model.ExerciseRequest
import com.vanphong.foodnfit.Model.Exercises
import com.vanphong.foodnfit.Model.OtpVerificationRequest
import com.vanphong.foodnfit.Model.StepsTracking
import com.vanphong.foodnfit.Model.StepsTrackingRequest
import com.vanphong.foodnfit.network.RetrofitClient
import com.vanphong.foodnfit.network.service.UploadResponse
import com.vanphong.foodnfit.util.safeCall
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import retrofit2.Response
import java.io.File

class StepsTrackingRepository {
    private val api = RetrofitClient.stepsTrackingService
    suspend fun saveSteps(request: StepsTrackingRequest): Result<StepsTracking> = safeCall {
        api.saveSteps(request)
    }
    suspend fun getAll(): Result<List<StepsTracking>> = safeCall {
        api.getAll()
    }
}