package com.vanphong.foodnfit.model

import java.time.LocalDateTime
import java.util.UUID

data class Feedback (
    val id: UUID,
    val userId: UUID,
    val message: String?,
    val submittedAt: LocalDateTime?,
    val adminId: UUID?,
    val answer: String?
)

data class FeedbackRequest(
    val message: String,
    val purpose: String,
    val inquiry: String?,
    val imageUrl: String?,
)

data class FeedbackResponse(
    val id: String,
    val userId: String,
    val message: String,
    val purpose: String,
    val inquiry: String,
    val submittedAt: String,
    val imageUrl: String,
    val status: Boolean
)

