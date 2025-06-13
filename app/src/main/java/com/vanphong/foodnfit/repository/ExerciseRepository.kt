package com.vanphong.foodnfit.repository

import com.vanphong.foodnfit.Model.ExerciseRequest
import com.vanphong.foodnfit.Model.Exercises
import com.vanphong.foodnfit.network.RetrofitClient
import com.vanphong.foodnfit.network.service.UploadResponse
import com.vanphong.foodnfit.util.safeCall
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
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

    suspend fun createExercise(request: ExerciseRequest): Response<Exercises>{
        return api.createExercise(request)
    }

    suspend fun getAll(): List<Exercises>?{
        val response = api.getAll()
        return if(response.isSuccessful) {
            response.body()
        }
        else{
            null
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
}