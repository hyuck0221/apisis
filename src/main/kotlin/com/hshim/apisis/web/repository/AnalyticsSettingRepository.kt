package com.hshim.apisis.web.repository

import com.hshim.apisis.web.entity.AnalyticsSetting
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.time.LocalDate

@Repository
interface AnalyticsSettingRepository : JpaRepository<AnalyticsSetting, String> {
    fun findByUserId(userId: String): AnalyticsSetting?
    fun findAllTop10ByNextAnalyticsDateLessThanEqualOrderByNextAnalyticsDateAsc(date: LocalDate): List<AnalyticsSetting>
    fun deleteByUserId(userId: String)
}
