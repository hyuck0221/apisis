package com.hshim.apisis.web.scheduler

import com.hshim.apisis.web.repository.AnalyticsSettingRepository
import com.hshim.apisis.web.service.AnalyticsCommandService
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import java.time.LocalDate

@Component
class AnalyticsScheduler(
    private val analyticsSettingRepository: AnalyticsSettingRepository,
    private val analyticsCommandService: AnalyticsCommandService
) {

    private val log: Logger = LoggerFactory.getLogger(AnalyticsScheduler::class.java)

    @Scheduled(fixedDelay = 1000 * 10)
    fun analytics() {
        val now = LocalDate.now()
        val settings = analyticsSettingRepository.findAllTop10ByNextAnalyticsDateLessThanEqualOrderByNextAnalyticsDateAsc(now)
        if (settings.isEmpty()) return
        log.info("[Analytics] analytics start")
        settings.forEach {
            analyticsCommandService.analytics(it.user.id)
        }
        log.info("[Analytics] analytics finish")
    }
}