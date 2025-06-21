package com.vanphong.foodnfit.model

import java.time.LocalDateTime
import java.util.UUID

data class StepsTracking (
    val id: Int,
    val userId: UUID,
    val stepsCount: Int,
    val startTime: String,
    val endTime: String,
    val isWalkingSession: Boolean,
    val distance: Float
)

data class StepsTrackingRequest(
    val stepsCount: Int,
    val startTime: String,  // <- đổi sang String
    val endTime: String,
    val isWalkingSession: Boolean = false,
    val distance: Float
)

data class StepSummary(
    val date: String,         // hoặc LocalDate nếu bạn dùng Java Time Adapter
    val totalSteps: Long,
    val totalDistance: Float
)

data class HourlyStepSummary(val hour: Int, val totalSteps: Int, val totalDistance: Float, val sessionCount: Int)