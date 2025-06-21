package com.vanphong.foodnfit.repository

import com.vanphong.foodnfit.model.MonthlyProfileResponse
import com.vanphong.foodnfit.model.UserProfileRequest
import com.vanphong.foodnfit.model.UserProfileResponse
import com.vanphong.foodnfit.network.RetrofitClient
import com.vanphong.foodnfit.network.service.UploadResponse
import com.vanphong.foodnfit.util.safeCall
import com.vanphong.foodnfit.util.safeCallString
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import retrofit2.Response
import java.io.File

class UserProfileRepository {
    private val profileApi = RetrofitClient.profileService
    private val uploadApi = RetrofitClient.uploadService

    suspend fun create(request: UserProfileRequest): Result<String> = safeCallString {
        profileApi.create(request)
    }

    suspend fun getLatest(): Result<UserProfileResponse> = safeCall {
        profileApi.getLatest()
    }

    suspend fun uploadImageFromFile(file: File): Response<UploadResponse> {
        val requestFile = file.asRequestBody("image/*".toMediaTypeOrNull())
        val body = MultipartBody.Part.createFormData("file", file.name, requestFile)
        return uploadApi.uploadImage(body)
    }

    suspend fun getMonthlyStatistics(year: Int, month: Int): Result<MonthlyProfileResponse> = safeCall {
        profileApi.getMonthlyStatistics(year, month)
    }
}