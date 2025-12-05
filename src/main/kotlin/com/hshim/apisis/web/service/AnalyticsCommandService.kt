package com.hshim.apisis.web.service

import com.hshim.apisis.web.entity.Analytics
import com.hshim.apisis.web.enums.Prompt
import com.hshim.apisis.web.repository.AnalyticsRepository
import com.hshim.apisis.web.repository.AnalyticsSettingRepository
import com.hshim.kemi.GeminiGenerator
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import util.ClassUtil.classToJson
import util.DateUtil.dateToString

@Service
@Transactional
class AnalyticsCommandService(
    private val apiKeyStatsService: ApiKeyStatsService,
    private val analyticsRepository: AnalyticsRepository,
    private val analyticsSettingRepository: AnalyticsSettingRepository,
    private val geminiGenerator: GeminiGenerator,
) {
    private val log: Logger = LoggerFactory.getLogger(AnalyticsCommandService::class.java)

    fun analytics(userId: String) {
        val setting = analyticsSettingRepository.findByUserId(userId) ?: return

        val searchStartDate = setting.dateRange.toSearchStartDate()
        val searchEndDate = setting.dateRange.toSearchEndDate()

        val apiStatistics = apiKeyStatsService.getUsageStatistics(
            userId = userId,
            period = "custom",
            startDate = searchStartDate.dateToString(),
            endDate = searchEndDate.dateToString(),
        )

        val response = geminiGenerator.ask(
            question = apiStatistics.classToJson(),
            prompt = Prompt.ANALYTICS.message,
        ) ?: return

        val html = response.replaceFirst("```html", "").replace("```", "")
        setting.nextAnalyticsDate = setting.dateRange.toAnalyticsDate()
        analyticsRepository.save(Analytics(userId, html, searchStartDate, searchEndDate))

        log.info("[Analytics] userId: $userId Analytics finish")
    }

    fun delete(id: String) {
        analyticsRepository.deleteById(id)
    }

    fun deleteAllByUserId(userId: String) {
        analyticsRepository.deleteAllByUserId(userId)
    }
}