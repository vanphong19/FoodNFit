package com.vanphong.foodnfit.util

import java.time.LocalDateTime
import java.time.temporal.ChronoUnit

fun parseServingSize(serving: String): Pair<Float, String> {
    val regex = Regex("""([\d.]+)\s*([a-zA-Z]+)""") // Cho phép khoảng trắng giữa số và đơn vị
    val matchResult = regex.find(serving.trim())

    val number = matchResult?.groups?.get(1)?.value?.toFloatOrNull() ?: 0f
    val unit = matchResult?.groups?.get(2)?.value ?: ""

    return Pair(number, unit)
}
fun getRelativeTimeString(time: LocalDateTime): String {
    val now = LocalDateTime.now() // Lấy thời gian hiện tại
    val years = ChronoUnit.YEARS.between(time, now)
    val months = ChronoUnit.MONTHS.between(time, now)
    val days = ChronoUnit.DAYS.between(time, now)
    val hours = ChronoUnit.HOURS.between(time, now)
    val minutes = ChronoUnit.MINUTES.between(time, now)

    return when {
        years > 0 -> "$years năm trước"
        months > 0 -> "$months tháng trước"
        days > 7 -> "${days / 7} tuần trước"
        days > 0 -> "$days ngày trước"
        hours > 0 -> "$hours giờ trước"
        minutes > 0 -> "$minutes phút trước"
        else -> "Vừa xong"
    }
}