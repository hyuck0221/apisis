package com.hshim.apisis.api.escape.service

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
    private val escapeThemeCommandService: EscapeThemeCommandService,
) {

    private val log: Logger = LoggerFactory.getLogger(EscapeThemeMigrationScheduler::class.java)

    @EventListener(ApplicationReadyEvent::class)
    fun initialSetting() {
        val top = escapeThemeRepository.findTopBy()
        if (top != null) return
        log.info("[Escape] initial setting start")
        escapeThemeCommandService.migration()
        log.info("[Escape] initial setting finish")
    }
}