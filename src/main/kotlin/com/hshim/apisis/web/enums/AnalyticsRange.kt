package com.hshim.apisis.web.enums

import java.time.LocalDate
import java.time.temporal.TemporalAdjusters

enum class AnalyticsRange(val description: String) {
    DAY("일"),
    WEEK("주"),
    MONTH("개월");

    fun toAnalyticsDate(): LocalDate {
        val now = LocalDate.now()
        return when (this) {
            DAY -> now.plusDays(1)
            WEEK -> if (now.dayOfWeek == java.time.DayOfWeek.MONDAY) now
            else now.with(TemporalAdjusters.next(java.time.DayOfWeek.MONDAY))

            MONTH -> if (now.dayOfMonth == 1) now else now.plusMonths(1).withDayOfMonth(1)
        }
    }
}