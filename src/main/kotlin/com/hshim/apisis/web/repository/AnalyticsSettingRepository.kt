package com.hshim.apisis.web.repository

import com.hshim.apisis.web.entity.AnalyticsSetting
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface AnalyticsSettingRepository : JpaRepository<AnalyticsSetting, String> {
    fun findByUserId(userID: String): AnalyticsSetting?
}
