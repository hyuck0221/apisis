package com.hshim.apisis.api.lotto.service

import com.hshim.apisis.api.lotto.model.LottoResponse
import com.hshim.apisis.api.lotto.repository.LottoRepository
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class LottoCommandService(
    private val lottoQueryService: LottoQueryService,
    private val lottoRepository: LottoRepository,
) {
    private val log: Logger = LoggerFactory.getLogger(LottoCommandService::class.java)

    fun init(times: Int): LottoResponse? {
        val entity = lottoQueryService.findByOpenAPI(times)?.toEntity() ?: return null
        return LottoResponse(lottoRepository.save(entity))
    }

    fun migration() {
        var times = lottoRepository.findTopByOrderByTimesDesc()
            ?.let { it.times + 1 }
            ?: 1

        var isEnd = false
        while (!isEnd) {
            val response = init(times++)
            isEnd = response == null
            response?.times?.let { log.info("[Lotto] $it times info saved") }
        }
    }
}