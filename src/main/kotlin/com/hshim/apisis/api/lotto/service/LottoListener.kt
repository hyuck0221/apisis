package com.hshim.apisis.api.lotto.service

import com.hshim.apisis.api.escape.scheduler.EscapeThemeMigrationScheduler
import com.hshim.apisis.api.lotto.repository.LottoRepository
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component

@Component
class LottoListener(
    private val lottoRepository: LottoRepository,
    private val lottoCommandService: LottoCommandService
) {

    private val log: Logger = LoggerFactory.getLogger(EscapeThemeMigrationScheduler::class.java)

    @EventListener(ApplicationReadyEvent::class)
    fun initialSetting() {
        val top = lottoRepository.findTopByOrderByTimesDesc()
        if (top != null) return
        log.info("[Lotto] initial setting start")
        lottoCommandService.migration()
        log.info("[Lotto] initial setting finish")
    }
}