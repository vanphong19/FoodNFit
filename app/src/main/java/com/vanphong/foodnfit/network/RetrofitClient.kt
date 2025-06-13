package com.vanphong.foodnfit.network

import android.content.Context
import com.vanphong.foodnfit.authenticator.TokenAuthenticator
import com.vanphong.foodnfit.interceptor.AuthInterceptor
import com.vanphong.foodnfit.network.service.AuthService
import com.vanphong.foodnfit.network.service.ExerciseService
import com.vanphong.foodnfit.network.service.FcmTokenService
import com.vanphong.foodnfit.network.service.FeedbackService
import com.vanphong.foodnfit.network.service.FoodItemService
import com.vanphong.foodnfit.network.service.StepsTrackingService
import com.vanphong.foodnfit.network.service.UploadService
import com.vanphong.foodnfit.network.service.UserService
import com.vanphong.foodnfit.repository.AuthRepository
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create
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
    fun init(context: Context, onLogout: () -> Unit) {
        okHttpClient = OkHttpClient.Builder()
            .addInterceptor(AuthInterceptor(context, onLogout))
            .authenticator(TokenAuthenticator(context)) // Bỏ authRepository ra, dùng cách khác để refresh
            .connectTimeout(30, TimeUnit.SECONDS)  // thời gian chờ kết nối
            .readTimeout(60, TimeUnit.SECONDS)     // thời gian chờ nhận phản hồi từ server
            .writeTimeout(30, TimeUnit.SECONDS)    // thời gian chờ gửi request lên server
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl("https://foodnfit-be.onrender.com/api/")
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        authService = retrofit.create(AuthService::class.java)
        exerciseService = retrofit.create(ExerciseService::class.java)
        uploadService = retrofit.create(UploadService::class.java)
        fcmTokenService = retrofit.create(FcmTokenService::class.java)
        stepsTrackingService = retrofit.create(StepsTrackingService::class.java)
        foodItemService = retrofit.create(FoodItemService::class.java)
        userService = retrofit.create(UserService::class.java)
        feedbackService = retrofit.create(FeedbackService::class.java)
    }
}
