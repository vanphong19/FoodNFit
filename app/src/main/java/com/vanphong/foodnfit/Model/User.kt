package com.vanphong.foodnfit.Model

import com.google.gson.annotations.SerializedName

data class User(
    @SerializedName("Id") val id: String,
    @SerializedName("Email") val email: String,
    @SerializedName("PasswordHash") val passwordHash: String,
    @SerializedName("FullName") val fullName: String,
    @SerializedName("Gender") val gender: Boolean,
    @SerializedName("Birthday") val birthday: String?,
    @SerializedName("AvatarUrl") val avatarUrl: String?,
    @SerializedName("CreatedDate") val createdDate: String?,
    @SerializedName("UpdatedDate") val updatedDate: String?,
    @SerializedName("IsActive") val isActive: Boolean
)