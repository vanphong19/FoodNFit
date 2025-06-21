package com.vanphong.foodnfit.repository

import com.vanphong.foodnfit.model.AuthResponse
import com.vanphong.foodnfit.model.ChangePasswordRequest
import com.vanphong.foodnfit.model.LoginRequest
import com.vanphong.foodnfit.model.OtpVerificationRequest
import com.vanphong.foodnfit.model.RefreshTokenRequest
import com.vanphong.foodnfit.model.RegisterRequest
import com.vanphong.foodnfit.model.ResendOtpRequest
import com.vanphong.foodnfit.model.ResetPasswordRequest
import com.vanphong.foodnfit.network.service.AuthService
import com.vanphong.foodnfit.util.safeCall
import com.vanphong.foodnfit.util.safeCallString

class AuthRepository(private val api: AuthService) {

    // ✅ Dùng safeCallString vì backend trả về Response<ResponseBody> dạng text
    suspend fun register(request: RegisterRequest): Result<String> = safeCallString {
        api.register(request)
    }

    suspend fun verifyOtp(request: OtpVerificationRequest): Result<String> = safeCallString {
        api.verifyOtp(request)
    }

    suspend fun resendOtp(request: ResendOtpRequest): Result<String> = safeCallString {
        api.resendOtp(request)
    }

    // ✅ Các API trả về JSON thì dùng safeCall chuẩn
    suspend fun login(request: LoginRequest): Result<AuthResponse> = safeCall {
        api.login(request)
    }

    suspend fun refreshToken(request: RefreshTokenRequest): Result<AuthResponse> = safeCall {
        api.refreshToken(request)
    }

    suspend fun getCurrentUser(token: String): Result<Map<String, Any>> = safeCall {
        api.me(token.withBearer())
    }

    suspend fun changePassword(token: String, request: ChangePasswordRequest): Result<String> = safeCallString {
        api.changePassword("Bearer $token", request)
    }

    suspend fun forgotPassword(request: ResendOtpRequest): Result<String> = safeCallString {
        api.forgotPassword(request)
    }

    suspend fun resetPassword(request: ResetPasswordRequest): Result<String> = safeCallString {
        api.resetPassword(request)
    }

    private fun String.withBearer(): String =
        if (startsWith("Bearer ")) this else "Bearer $this"
}
