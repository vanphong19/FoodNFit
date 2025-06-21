package com.vanphong.foodnfit.model

import com.google.gson.annotations.SerializedName
import java.time.LocalDate

data class User(
    val id: String,
    val email: String,
    val fullName: String,
    val passwordHash: String,
    val gender: Boolean,
    val birthday: String?,
    val avatarUrl: String?,
    val createdDate: String?,
    val updatedDate: String?,
    val active: Boolean,
    val blocked: Boolean
)

data class UserResponse(
    val id: String,
    val email: String,
    val fullname: String,
    val gender: Boolean,
    val birthday: String?,
    val avatarUrl: String?,
    val active: Boolean,
    val blocked: Boolean
)

data class UserRequest(
    val email: String,
    val password: String,
    val fullname: String,
    val gender: Boolean,
    val birthday: String?,
    val avatarUrl: String?,
)

data class UserUpdateRequest(
    val fullname: String,
    val gender: Boolean,
    val birthday: String?,
    val avatarUrl: String?,
)

data class UserResponseById(
    val id: String,
    val email: String,
    val fullname: String,
    val gender: Boolean,
    val birthday: String?,
    val avatarUrl: String?,
    val active: Boolean,
    val blocked: Boolean,
    val history: List<HistoryResponse>?
)

data class UserSetupInfo(
    var name: String? = null,
    var birthday: String? = null,
    var gender: Boolean? = null,
    var height: Float? = null,
    var weight: Float? = null,
    var goal: String? = null
)

data class UserDailyStatsDto(
    val caloriesIn: Double,
    val protein: Double,
    val carbs: Double,
    val fat: Double,
    val caloriesOut: Double,
    val tdee: Double
)


data class RegisterRequest(
    val email: String,
    val password: String,
    val confirmPassword: String
)

data class OtpVerificationRequest(val email: String, val otp: String)
data class ResendOtpRequest(val email: String)
data class LoginRequest(val email: String, val password: String)
data class AuthResponse(val accessToken: String, val refreshToken: String, val userId: String, val role: String)
data class RefreshTokenRequest(val refreshToken: String)
data class ChangePasswordRequest(
    @SerializedName("oldPassword") val oldPassword: String,
    @SerializedName("newPassword") val newPassword: String,
    @SerializedName("confirmNewPassword") val confirmNewPassword: String,
)

data class ResetPasswordRequest(val email: String, val otp: String, val newPassword: String)
