package com.vanphong.foodnfit.repository

import com.vanphong.foodnfit.model.ExerciseRequest
import com.vanphong.foodnfit.model.Exercises
import com.vanphong.foodnfit.model.FoodItemResponse
import com.vanphong.foodnfit.model.PageResponse
import com.vanphong.foodnfit.network.RetrofitClient
import com.vanphong.foodnfit.network.service.UploadResponse
import com.vanphong.foodnfit.util.safeCall
import com.vanphong.foodnfit.util.safeCallString
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import retrofit2.Response
import java.io.File

class ExerciseRepository {
    private val api = RetrofitClient.exerciseService
    private val uploadApi = RetrofitClient.uploadService

    suspend fun uploadImageFromFile(file: File): Response<UploadResponse> {
        val requestFile = file.asRequestBody("image/*".toMediaTypeOrNull())
        val body = MultipartBody.Part.createFormData("file", file.name, requestFile)
        return uploadApi.uploadImage(body)
    }

    suspend fun createExercise(request: ExerciseRequest): Result<Exercises> = safeCall {
        api.createExercise(request)
    }

    suspend fun getAllExercises(
        search: String? = "",
        page: Int = 0,
        size: Int = 10,
        sortBy: String = "id",
        sortDir: String = "asc"
    ): Result<PageResponse<Exercises>> {
        return safeCall {
            api.getAllExercises(search, page, size, sortBy, sortDir)
        }
    }

    suspend fun getById(id: Int): Exercises?{
        val response = api.getById(id)
        return if(response.isSuccessful){
            response.body()
        }
        else{
            null
        }
    }
    suspend fun count(): Result<Long> = safeCall {
        api.countExercise()
    }
    suspend fun countExerciseThisMonth():Result<Long> = safeCall {
        api.countExerciseThisMonth()
    }
    suspend fun update(id: Int, request: ExerciseRequest): Result<Exercises> = safeCall {
        api.update(id, request)
    }
    suspend fun remove(id: Int): Result<String> = safeCallString {
        api.remove(id)
    }
}