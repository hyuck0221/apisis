package com.hshim.apisis.api.escape.scheduler

import com.hshim.apisis.api.escape.service.EscapeThemeCommandService
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

@Component
class EscapeThemeMigrationScheduler(private val escapeThemeCommandService: EscapeThemeCommandService) {

    private val log: Logger = LoggerFactory.getLogger(EscapeThemeMigrationScheduler::class.java)

    @Scheduled(cron = "0 0 1 * * *", zone = "Asia/Seoul")
    fun migration() {
        log.info("[Escape] theme migration start===")
        escapeThemeCommandService.migration()
        log.info("[Escape] theme migration finish===")
    }

    @Scheduled(cron = "0 0 3 * * *", zone = "Asia/Seoul")
    fun updateSliceTheme() {
        log.info("[Escape] theme update start===")
        escapeThemeCommandService.updateSliceTheme()
        log.info("[Escape] theme update finish===")
    }
}