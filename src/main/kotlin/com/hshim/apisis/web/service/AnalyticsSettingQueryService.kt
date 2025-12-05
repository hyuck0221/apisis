package com.hshim.apisis.web.service

import com.hshim.apisis.web.entity.AnalyticsSetting
import com.hshim.apisis.web.repository.AnalyticsSettingRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class AnalyticsSettingQueryService(private val analyticsSettingRepository: AnalyticsSettingRepository) {
    fun findBy(userId: String): AnalyticsSetting? {
        return analyticsSettingRepository.findByUserId(userId)
    }
}