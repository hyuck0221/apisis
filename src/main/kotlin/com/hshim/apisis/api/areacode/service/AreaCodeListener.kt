package com.hshim.apisis.api.areacode.service

import com.hshim.apisis.api.areacode.repository.AreaCodeRepository
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component

@Component
class AreaCodeListener(
    private val areaCodeRepository: AreaCodeRepository,
    private val areCodeCommandService: AreaCodeCommandService,
) {
    private val log: Logger = LoggerFactory.getLogger(AreaCodeListener::class.java)

    @EventListener(ApplicationReadyEvent::class)
    fun initialSetting() {
        val topAreaCode = areaCodeRepository.findTopBy()
        if (topAreaCode == null) {
            log.info("[Area Code] initial setting start")
            areCodeCommandService.parsing()
            log.info("[Area Code] initial setting finish")
        }
    }
}