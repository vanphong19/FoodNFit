package com.vanphong.foodnfit.network.service

import com.vanphong.foodnfit.model.OtpVerificationRequest
import com.vanphong.foodnfit.model.RegisterRequest
import com.vanphong.foodnfit.model.ResendOtpRequest
import com.vanphong.foodnfit.model.LoginRequest
import com.vanphong.foodnfit.model.AuthResponse
import com.vanphong.foodnfit.model.ChangePasswordRequest
import com.vanphong.foodnfit.model.RefreshTokenRequest
import com.vanphong.foodnfit.model.ResetPasswordRequest


import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.GET
import retrofit2.http.Header

import okhttp3.ResponseBody

interface AuthService {

    @POST("auth/register")
    suspend fun register(@Body request: RegisterRequest): Response<ResponseBody>

    @POST("auth/verify-otp")
    suspend fun verifyOtp(@Body request: OtpVerificationRequest): Response<ResponseBody>

    @POST("auth/resend-otp")
    suspend fun resendOtp(@Body request: ResendOtpRequest): Response<ResponseBody>

    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): Response<AuthResponse>

    @POST("auth/refresh-token")
    suspend fun refreshToken(@Body request: RefreshTokenRequest): Response<AuthResponse>

    @GET("auth/me")
    suspend fun me(@Header("Authorization") token: String): Response<Map<String, Any>>

    @POST("auth/change-password")
    suspend fun changePassword(
        @Header("Authorization") token: String,
        @Body request: ChangePasswordRequest
    ): Response<ResponseBody>

    @POST("auth/forgot-password")
    suspend fun forgotPassword(@Body request: ResendOtpRequest): Response<ResponseBody>

    @POST("auth/reset-password")
    suspend fun resetPassword(@Body request: ResetPasswordRequest): Response<ResponseBody>
}
