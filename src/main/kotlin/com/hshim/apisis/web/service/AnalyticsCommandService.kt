package com.hshim.apisis.web.service

import com.hshim.apisis.web.repository.AnalyticsRepository
import com.hshim.apisis.web.repository.AnalyticsSettingRepository
import com.hshim.apisis.web.repository.ApiCallLogRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class AnalyticsCommandService(
    private val apiCallLogRepository: ApiCallLogRepository,
    private val analyticsRepository: AnalyticsRepository,
    private val analyticsSettingRepository: AnalyticsSettingRepository
) {
    fun analytics(userId: String) {
//        analyticsRepository.findByUserId(userId)
//        analyticsSettingRepository.findByUserId(userId)
//        val apiCallLogs = apiCallLogRepository.findAllByUserIdAndCalledAtBetween(userId)
    }
}