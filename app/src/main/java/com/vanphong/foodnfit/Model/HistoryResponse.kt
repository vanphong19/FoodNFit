package com.vanphong.foodnfit.Model

import java.time.LocalDate

data class HistoryResponse(
    val id: String,
    val email: String,
    val fullname: String,
    val gender: Boolean,
    val birthday: String?,
    val avatarUrl: String?,
    val isActive: Boolean,
    val changedAt: String,
    val changeType: String,
    val changedBy: String
)