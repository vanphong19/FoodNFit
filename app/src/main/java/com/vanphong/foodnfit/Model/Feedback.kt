package com.vanphong.foodnfit.Model

import java.time.LocalDateTime
import java.util.Date
import java.util.UUID

data class Feedback (
    val id: UUID,
    val userId: UUID,
    val message: String?,
    val submittedAt: LocalDateTime?,
    val adminId: UUID?,
    val answer: String?
)