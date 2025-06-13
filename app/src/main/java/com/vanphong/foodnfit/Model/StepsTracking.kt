package com.vanphong.foodnfit.Model

import java.time.LocalDateTime
import java.util.Date
import java.util.UUID

data class StepsTracking (
    val id: Int,
    val userId: UUID,
    val stepCount: Int,
    val startTime: LocalDateTime,
    val endTime: LocalDateTime
)

data class StepsTrackingRequest(
    val stepCount: Int,
    val startTime: LocalDateTime,
    val endTime: LocalDateTime,
)