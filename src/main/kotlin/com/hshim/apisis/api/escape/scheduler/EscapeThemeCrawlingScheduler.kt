package com.hshim.apisis.api.escape.scheduler

import com.hshim.apisis.api.escape.service.EscapeThemeCommandService
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

@Component
class EscapeThemeCrawlingScheduler(private val escapeThemeCommandService: EscapeThemeCommandService) {

    @Scheduled(cron = "0 0 1 * * *", zone = "Asia/Seoul")
    fun crawling() {
        escapeThemeCommandService.crawling()
    }
}