package com.vanphong.foodnfit.interceptor

import android.content.Context
import android.util.Log
import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException
import java.net.SocketTimeoutException
import java.net.ConnectException

class AuthInterceptor(
    private val context: Context,
    private val onLogout: () -> Unit
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val prefs = context.getSharedPreferences("auth", Context.MODE_PRIVATE)
        val token = prefs.getString("access_token", null)

        val requestBuilder = chain.request().newBuilder()
        if (!token.isNullOrEmpty()) {
            requestBuilder.addHeader("Authorization", "Bearer $token")
        }

        return try {
            val response = chain.proceed(requestBuilder.build())

            // Xử lý response codes
            when (response.code) {
                401 -> {
                    handleUnauthorized(response)
                }
                403 -> {
                    handleForbidden(response)
                }
                500 -> {
                    // Server error - có thể do cold start
                    val errorBody = response.peekBody(1024).string()
                    Log.w("AuthInterceptor", "500 Internal Server Error - possibly cold start: $errorBody")
                    // KHÔNG logout - để retry logic xử lý
                }
                502 -> {
                    Log.w("AuthInterceptor", "502 Bad Gateway - server starting up")
                }
                503 -> {
                    Log.w("AuthInterceptor", "503 Service Unavailable - server overloaded or starting")
                }
                504 -> {
                    Log.w("AuthInterceptor", "504 Gateway Timeout - server taking too long to respond")
                }
                in 200..299 -> {
                    Log.d("AuthInterceptor", "Success: ${response.code} for ${chain.request().url}")
                }
                else -> {
                    Log.d("AuthInterceptor", "Response: ${response.code} for ${chain.request().url}")
                }
            }

            response

        } catch (e: IOException) {
            when (e) {
                is SocketTimeoutException -> {
                    Log.e("AuthInterceptor", "Request timeout - possibly cold start: ${e.message}")
                    // KHÔNG logout - có thể là cold start
                }
                is ConnectException -> {
                    Log.e("AuthInterceptor", "Connection failed - server might be sleeping: ${e.message}")
                    // KHÔNG logout - server có thể đang sleep
                }
                else -> {
                    Log.e("AuthInterceptor", "Network error: ${e.message}")
                }
            }
            throw e
        }
    }

    private fun handleUnauthorized(response: Response) {
        val errorBody = response.peekBody(2048).string()
        Log.e("AuthInterceptor", "401 Unauthorized: $errorBody")

        // Chỉ logout khi thực sự là lỗi authentication
        val isAuthError = errorBody.contains("token expired", ignoreCase = true) ||
                errorBody.contains("expired", ignoreCase = true) ||
                errorBody.contains("invalid token", ignoreCase = true) ||
                errorBody.contains("unauthorized", ignoreCase = true)

        if (isAuthError) {
            Log.w("AuthInterceptor", "Authentication error detected → triggering logout")
            try {
                onLogout()
            } catch (e: Exception) {
                Log.e("AuthInterceptor", "Error during logout: ${e.message}")
            }
        } else {
            Log.d("AuthInterceptor", "401 without auth error - might be server issue")
        }
    }

    private fun handleForbidden(response: Response) {
        val errorBody = response.peekBody(2048).string()
        Log.e("AuthInterceptor", "403 Forbidden: $errorBody")

        // Chỉ logout khi thực sự là lỗi token
        val isTokenError = errorBody.contains("invalid token", ignoreCase = true) ||
                errorBody.contains("forbidden", ignoreCase = true) ||
                errorBody.contains("access denied", ignoreCase = true)

        if (isTokenError) {
            Log.w("AuthInterceptor", "Token error detected → triggering logout")
            try {
                onLogout()
            } catch (e: Exception) {
                Log.e("AuthInterceptor", "Error during logout: ${e.message}")
            }
        } else {
            Log.d("AuthInterceptor", "403 without token error - might be permission issue")
        }
    }
}