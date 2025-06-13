package com.vanphong.foodnfit.util

import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import java.time.temporal.ChronoField

object DateUtils {

    private val fallbackInputPatterns = listOf(
        "yyyy-MM-dd'T'HH:mm:ss",       // ISO_DATE_TIME (mặc định)
        "yyyy-MM-dd HH:mm:ss",         // kiểu bạn nêu
        "yyyy-MM-dd",                  // chỉ ngày
        "dd-MM-yyyy HH:mm",            // định dạng khác
        "dd/MM/yyyy HH:mm",
        "yyyy-MM-dd HH:mm:ss.SSSSSS"
    )

    fun formatDateString(
        input: String?,
        outputPattern: String = "dd/MM/yyyy",
        inputPattern: String? = null
    ): String {
        if (input.isNullOrBlank()) return "Không rõ"

        val outputFormatter = DateTimeFormatter.ofPattern(outputPattern)

        val inputFormatters = if (!inputPattern.isNullOrBlank()) {
            listOf(DateTimeFormatter.ofPattern(inputPattern))
        } else {
            fallbackInputPatterns.mapNotNull {
                try {
                    DateTimeFormatter.ofPattern(it)
                } catch (e: Exception) {
                    null
                }
            }
        }

        for (formatter in inputFormatters) {
            try {
                val temporalAccessor = formatter.parseBest(input, LocalDateTime::from, LocalDate::from)

                return if (temporalAccessor.isSupported(ChronoField.HOUR_OF_DAY)) {
                    val ldt = LocalDateTime.from(temporalAccessor)
                    ldt.format(outputFormatter)
                } else {
                    val ld = LocalDate.from(temporalAccessor)
                    ld.format(outputFormatter)
                }
            } catch (e: DateTimeParseException) {
                // Thử formatter tiếp theo
            }
        }

        return "Không rõ"
    }
}