package com.hshim.apisis.api.lotto.scheduler

import com.hshim.apisis.api.escape.scheduler.EscapeThemeMigrationScheduler
import com.hshim.apisis.api.lotto.service.LottoCommandService
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

@Component
class LottoScheduler(private val lottoCommandService: LottoCommandService) {

    private val log: Logger = LoggerFactory.getLogger(EscapeThemeMigrationScheduler::class.java)

    @Scheduled(cron = "0 0 21 ? * SAT", zone = "Asia/Seoul")
    fun migration() {
        log.info("[Lotto] migration start")
        lottoCommandService.migration()
        log.info("[Lotto] theme migration finish")
    }
}