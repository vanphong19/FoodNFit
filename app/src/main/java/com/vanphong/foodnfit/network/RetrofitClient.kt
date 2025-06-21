package com.vanphong.foodnfit.network

import android.content.Context
import android.util.Log
import com.vanphong.foodnfit.BuildConfig
import com.vanphong.foodnfit.authenticator.TokenAuthenticator
import com.vanphong.foodnfit.interceptor.AuthInterceptor
import com.vanphong.foodnfit.network.service.*
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create
import java.io.IOException
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.util.concurrent.TimeUnit

object RetrofitClient {
    private lateinit var retrofit: Retrofit
    private lateinit var okHttpClient: OkHttpClient

    lateinit var authService: AuthService
        private set
    lateinit var exerciseService: ExerciseService
        private set
    lateinit var uploadService: UploadService
        private set
    lateinit var fcmTokenService: FcmTokenService
        private set
    lateinit var stepsTrackingService: StepsTrackingService
        private set
    lateinit var userService: UserService
        private set
    lateinit var foodItemService: FoodItemService
        private set
    lateinit var feedbackService: FeedbackService
        private set
    lateinit var foodTypeService: FoodTypeService
        private set
    lateinit var profileService: UserProfileService
        private set
    lateinit var waterIntakeService: WaterIntakeService
        private set
    lateinit var foodLogService: FoodLogService
        private set
    lateinit var workoutPlanService: WorkoutPlanService
        private set

    lateinit var userGoalService: UserGoalService
        private set
    lateinit var foodLogDetailService: FoodLogDetailService
        private set
    lateinit var workoutExerciseService: WorkoutExerciseService
        private set
    lateinit var reminderService: ReminderService
        private set

    fun init(context: Context, onLogout: () -> Unit) {
        okHttpClient = OkHttpClient.Builder()
            .addInterceptor(AuthInterceptor(context, onLogout))
            .authenticator(TokenAuthenticator(context))

            // Tăng timeout cao hơn cho cold start (Render có thể mất tới 90s)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(90, TimeUnit.SECONDS) // Tăng lên 90s
            .writeTimeout(60, TimeUnit.SECONDS)

            // Logging interceptor
            .addInterceptor(HttpLoggingInterceptor().apply {
                level = if (BuildConfig.DEBUG) {
                    HttpLoggingInterceptor.Level.BODY
                } else {
                    HttpLoggingInterceptor.Level.BASIC // Vẫn log basic info trong production
                }
            })

            // Enhanced retry interceptor với cold start handling
            .addInterceptor { chain ->
                val request = chain.request()
                var response: okhttp3.Response? = null
                var lastException: IOException? = null

                // Retry logic với backoff strategy
                val maxRetries = when {
                    request.method == "GET" -> 3
                    request.method == "POST" && isIdempotentEndpoint(request.url.toString()) -> 2
                    else -> 1
                }

                for (attempt in 0..maxRetries) {
                    try {
                        if (attempt > 0) {
                            Log.i("RetrofitClient", "Retry attempt $attempt for ${request.url}")
                        }

                        response = chain.proceed(request)
                        val resp = response!!

                        // Kiểm tra response codes
                        when (resp.code) {
                            200, 201, 202 -> {
                                Log.d("RetrofitClient", "Success: ${resp.code} for ${request.url}")
                                return@addInterceptor resp
                            }
                            500, 502, 503, 504 -> {
                                // Server errors - có thể do cold start
                                if (attempt < maxRetries) {
                                    Log.w("RetrofitClient", "Server error ${resp.code}, retrying... (attempt ${attempt + 1})")
                                    resp.close() // Đóng response để tránh memory leak

                                    // Exponential backoff: 2s, 4s, 8s
                                    val delay = (2000L * (1 shl attempt)).coerceAtMost(8000L)
                                    Thread.sleep(delay)

                                    continue // Retry
                                } else {
                                    Log.e("RetrofitClient", "Max retries reached for server error ${resp.code}")
                                    return@addInterceptor resp
                                }
                            }
                            else -> {
                                // Other status codes (401, 404, etc.) - không retry
                                return@addInterceptor resp
                            }
                        }

                    } catch (e: IOException) {
                        lastException = e

                        val shouldRetry = when (e) {
                            is SocketTimeoutException -> {
                                Log.w("RetrofitClient", "Timeout on attempt ${attempt + 1}: ${e.message}")
                                true // Retry timeout (có thể do cold start)
                            }
                            is ConnectException -> {
                                Log.w("RetrofitClient", "Connection failed on attempt ${attempt + 1}: ${e.message}")
                                true // Retry connection errors
                            }
                            else -> {
                                Log.w("RetrofitClient", "IO error on attempt ${attempt + 1}: ${e.message}")
                                false // Không retry các lỗi khác
                            }
                        }

                        if (shouldRetry && attempt < maxRetries) {
                            // Exponential backoff với jitter
                            val baseDelay = 2000L * (1 shl attempt)
                            val jitter = (Math.random() * 1000).toLong() // Random 0-1s
                            val delay = (baseDelay + jitter).coerceAtMost(10000L)

                            Log.i("RetrofitClient", "Waiting ${delay}ms before retry...")
                            Thread.sleep(delay)
                        } else {
                            break // Không retry nữa
                        }
                    }
                }

                // Nếu tất cả attempts đều fail
                response?.let { return@addInterceptor it }
                throw lastException ?: IOException("Unknown error after $maxRetries retries")
            }

            .build()

        retrofit = Retrofit.Builder()
            .baseUrl("https://foodnfit-be.onrender.com/api/")
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        // Initialize services
        authService = retrofit.create(AuthService::class.java)
        exerciseService = retrofit.create(ExerciseService::class.java)
        uploadService = retrofit.create(UploadService::class.java)
        fcmTokenService = retrofit.create(FcmTokenService::class.java)
        stepsTrackingService = retrofit.create(StepsTrackingService::class.java)
        foodItemService = retrofit.create(FoodItemService::class.java)
        userService = retrofit.create(UserService::class.java)
        feedbackService = retrofit.create(FeedbackService::class.java)
        foodTypeService = retrofit.create(FoodTypeService::class.java)
        profileService = retrofit.create(UserProfileService::class.java)
        waterIntakeService = retrofit.create(WaterIntakeService::class.java)
        foodLogService = retrofit.create(FoodLogService::class.java)
        workoutPlanService = retrofit.create(WorkoutPlanService::class.java)
        userGoalService = retrofit.create(UserGoalService::class.java)
        foodLogDetailService = retrofit.create(FoodLogDetailService::class.java)
        workoutExerciseService = retrofit.create(WorkoutExerciseService::class.java)
        reminderService = retrofit.create(ReminderService::class.java)
    }

    /**
     * Kiểm tra xem endpoint có phải là idempotent không (an toàn để retry)
     */
    private fun isIdempotentEndpoint(url: String): Boolean {
        val idempotentEndpoints = listOf(
            "/auth/refresh", // Refresh token
            "/user/profile", // Get/Update profile
            "/fcm/token"     // Update FCM token
        )
        return idempotentEndpoints.any { url.contains(it) }
    }

    /**
     * Health check để warm up server
     */
    suspend fun warmUpServer(): Boolean {
        return try {
            // Thực hiện một request đơn giản để wake up server
            val response = okHttpClient.newCall(
                okhttp3.Request.Builder()
                    .url("https://foodnfit-be.onrender.com/api/exercises/getAll")
                    .get()
                    .build()
            ).execute()

            val success = response.isSuccessful
            Log.i("RetrofitClient", "Server warm-up ${if (success) "successful" else "failed"}")
            response.close()
            success
        } catch (e: Exception) {
            Log.w("RetrofitClient", "Server warm-up failed: ${e.message}")
            false
        }
    }
}