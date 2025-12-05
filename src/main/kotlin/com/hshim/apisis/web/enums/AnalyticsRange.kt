package com.hshim.apisis.web.enums

import java.time.DayOfWeek
import java.time.LocalDate
import java.time.temporal.TemporalAdjusters

enum class AnalyticsRange(val description: String) {
    DAY("일"),
    WEEK("주"),
    MONTH("개월");

    fun toAnalyticsDate(date: LocalDate = LocalDate.now()): LocalDate {
        return when (this) {
            DAY -> date.plusDays(1)
            WEEK -> date.with(TemporalAdjusters.next(DayOfWeek.MONDAY))
            MONTH -> date.plusMonths(1).withDayOfMonth(1)
        }
    }

    fun toSearchStartDate(date: LocalDate = LocalDate.now()): LocalDate {
        return when (this) {
            DAY -> date.minusDays(1)
            WEEK -> date.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY)).minusWeeks(1)
            MONTH -> date.minusMonths(1).withDayOfMonth(1)
        }
    }

    fun toSearchEndDate(date: LocalDate = LocalDate.now()): LocalDate {
        return when (this) {
            DAY -> date.minusDays(1)
            WEEK -> date.with(TemporalAdjusters.previous(DayOfWeek.SUNDAY))
            MONTH -> date.withDayOfMonth(1).minusDays(1)
        }
    }
}