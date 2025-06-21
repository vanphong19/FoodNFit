package com.vanphong.foodnfit.repository

import com.vanphong.foodnfit.model.PageResponse
import com.vanphong.foodnfit.model.UserDailyStatsDto
import com.vanphong.foodnfit.model.UserRequest
import com.vanphong.foodnfit.model.UserResponse
import com.vanphong.foodnfit.model.UserResponseById
import com.vanphong.foodnfit.model.UserUpdateRequest
import com.vanphong.foodnfit.network.RetrofitClient
import com.vanphong.foodnfit.network.service.UploadResponse
import com.vanphong.foodnfit.util.safeCall
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import retrofit2.Response
import java.io.File

class UserRepository {

    private val userService = RetrofitClient.userService
    private val uploadApi = RetrofitClient.uploadService


    suspend fun countUsers(): Result<Long> {
        return safeCall {
            userService.countUser()
        }
    }

    suspend fun countUsersThisMonth(): Result<Long> {
        return safeCall {
            userService.getCountUserThisMonth()
        }
    }

    suspend fun getAllUsers(
        search: String? = "",
        gender: Boolean? = null,
        block: Boolean? = null,
        page: Int = 0,
        size: Int = 10,
        sortBy: String = "id",
        sortDir: String = "asc"
    ): Result<PageResponse<UserResponse>> {
        return safeCall {
            userService.getAllUsers(search, gender, block, page, size, sortBy, sortDir)
        }
    }

    suspend fun getUserById(id: String): Result<UserResponseById> = safeCall {
        userService.getUserById(id)
    }

    suspend fun updateUser(id: String, request: UserUpdateRequest): Result<UserResponse> = safeCall {
        userService.updateUser(id, request)
    }

    suspend fun deleteUser(id: String): Result<UserResponse> = safeCall {
        userService.deleteUser(id)
    }

    suspend fun lockUser(id: String): Result<UserResponse> = safeCall {
        userService.lockUser(id)
    }

    suspend fun countUsersLastMonth(): Result<Long> = safeCall {
        userService.countUsersLastMonth()
    }

    suspend fun uploadImageFromFile(file: File): Response<UploadResponse> {
        val requestFile = file.asRequestBody("image/*".toMediaTypeOrNull())
        val body = MultipartBody.Part.createFormData("file", file.name, requestFile)
        return uploadApi.uploadImage(body)
    }

    suspend fun createUser(request: UserRequest): Result<UserResponse> = safeCall {
        userService.createUser(request)
    }

    suspend fun getUserStatsToday(): Result<UserDailyStatsDto> = safeCall {
        userService.getUserStatsToday()
    }
}
