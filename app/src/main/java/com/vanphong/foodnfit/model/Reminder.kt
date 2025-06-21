package com.vanphong.foodnfit.model

import java.time.LocalDateTime
import java.util.UUID

data class Reminder (
    val id: Int,
    val userId: UUID,
    val reminderType: String?,
    val message: String?,
    val scheduledTime: LocalDateTime?,
    val isActive: Boolean,
    val isRead: Boolean
)

data class ReminderRequest(
    val reminderType: String,
    val message: String,
    val scheduledTime: String,
    val frequency: String
)

data class ReminderResponse(
    val id: Int,
    val reminderType: String,
    val message: String,
    val scheduledTime: String,
    val isActive: Boolean,
    val frequency: String
)