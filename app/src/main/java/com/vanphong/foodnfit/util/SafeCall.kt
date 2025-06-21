package com.vanphong.foodnfit.util

import okhttp3.ResponseBody
import retrofit2.Response

inline fun <T> safeCall(apiCall: () -> Response<T>): Result<T> {
    return try {
        val response = apiCall()

        if (response.isSuccessful) {
            val body = response.body()
            if (body != null) {
                Result.success(body)
            } else {
                Result.failure(Exception("Dữ liệu trả về rỗng từ server (HTTP ${response.code()})"))
            }
        } else {
            val errorBody = response.errorBody()?.string()
            val message = if (!errorBody.isNullOrBlank()) {
                "Lỗi từ server: $errorBody (HTTP ${response.code()})"
            } else {
                "Lỗi không xác định từ server (HTTP ${response.code()})"
            }
            Result.failure(Exception(message))
        }
    } catch (e: Exception) {
        Result.failure(Exception("Lỗi ngoại lệ khi gọi API: ${e.message}", e))
    }
}


inline fun safeCallString(apiCall: () -> Response<ResponseBody>): Result<String> {
    return try {
        val response = apiCall()
        if (response.isSuccessful) {
            val body = response.body()?.string()
            if (!body.isNullOrBlank()) {
                Result.success(body)
            } else {
                Result.failure(Exception("Phản hồi rỗng"))
            }
        } else {
            val errorMsg = response.errorBody()?.string() ?: "Lỗi không xác định từ server"
            Result.failure(Exception(errorMsg))
        }
    } catch (e: Exception) {
        Result.failure(e)
    }
}

suspend fun <T> safeApiCall(apiCall: suspend () -> Response<T>): Result<T> {
    return try {
        val response = apiCall()
        if (response.isSuccessful && response.body() != null) {
            Result.success(response.body()!!)
        } else {
            Result.failure(Exception("API Error: ${response.code()} ${response.message()}"))
        }
    } catch (e: Exception) {
        Result.failure(e)
    }
}
inline fun safeCallUnit(apiCall: () -> Response<*>): Result<Unit> {
    return try {
        val response = apiCall()
        if (response.isSuccessful) {
            Result.success(Unit)
        } else {
            val errorMsg = response.errorBody()?.string() ?: "Lỗi không xác định từ server"
            Result.failure(Exception(errorMsg))
        }
    } catch (e: Exception) {
        Result.failure(e)
    }
}


