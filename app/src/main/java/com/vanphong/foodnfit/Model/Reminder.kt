package com.vanphong.foodnfit.Model

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