package com.hshim.apisis.web.service

import com.hshim.apisis.web.model.AnalyticsSettingRequest
import com.hshim.apisis.web.repository.AnalyticsSettingRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class AnalyticsSettingCommandService(private val analyticsSettingRepository: AnalyticsSettingRepository) {
    fun update(userId: String, request: AnalyticsSettingRequest) {
        val setting = analyticsSettingRepository.findByUserId(userId)
        if (setting == null) analyticsSettingRepository.save(request.toEntity(userId))
        else request.updateTo(setting)
    }
}