package com.hshim.apisis.api.escape.service

import com.hshim.apisis.api.escape.repository.EscapeReviewRepository
import com.hshim.apisis.api.escape.repository.EscapeThemeRepository
import com.hshim.apisis.api.escape.scheduler.EscapeThemeMigrationScheduler
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component

@Component
class EscapeListener(
    private val escapeThemeRepository: EscapeThemeRepository,
    private val escapeReviewRepository: EscapeReviewRepository,
    private val escapeThemeCommandService: EscapeThemeCommandService,
    private val escapeReviewCommandService: EscapeReviewCommandService,
) {

    private val log: Logger = LoggerFactory.getLogger(EscapeThemeMigrationScheduler::class.java)

    @EventListener(ApplicationReadyEvent::class)
    fun initialSetting() {
        val topTheme = escapeThemeRepository.findTopBy()
        if (topTheme == null) {
            log.info("[Escape Theme] initial setting start")
            escapeThemeCommandService.migration()
            log.info("[Escape Theme] initial setting finish")
        }

        val topReview = escapeReviewRepository.findTopBy()
        if (topReview == null) {
            log.info("[Escape Review] initial setting start")
            escapeReviewCommandService.parsing()
            log.info("[Escape Review] initial setting finish")
        }
    }
}