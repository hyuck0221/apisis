package com.hshim.apisis.api.lotto.service

import com.hshim.apisis.api.lotto.entity.Lotto
import com.hshim.apisis.api.lotto.model.LottoDetailSearchCondition
import com.hshim.apisis.api.lotto.model.LottoNumberUrlDecodeResponse
import com.hshim.apisis.api.lotto.model.LottoOpenAPIResponse
import com.hshim.apisis.api.lotto.repository.LottoRepository
import com.hshim.apisis.common.util.QueueUtil.polls
import com.hshim.apisis.properties.LottoProperties
import org.springframework.data.repository.findByIdOrNull
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.client.RestTemplate
import org.springframework.web.server.ResponseStatusException
import util.ClassUtil.jsonToClass
import java.util.*

@Service
@Transactional(readOnly = true)
class LottoQueryService(
    private val properties: LottoProperties,
    private val lottoRepository: LottoRepository,
) {
    private val restTemplate = RestTemplate()
    private val separator = "v="
    private val parameterSeparators = listOf('q', 'm', 's')

    fun findByOpenAPI(times: Int): LottoOpenAPIResponse? {
        return try {
            restTemplate.exchange(
                properties.apiUrl + "&drwNo=$times",
                HttpMethod.GET,
                null,
                String::class.java
            ).body?.jsonToClass()
        } catch (e: Exception) {
            return null
        }
    }

    fun findLottoInfoByUrl(url: String): List<LottoNumberUrlDecodeResponse> {

        val record = when (val index = url.lastIndexOf(separator)) {
            -1 -> throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid URL")
            else -> url.substring(index + separator.length, url.length)
        }

        val queue: Queue<Char> = LinkedList(record.toList())
        val times = queue.polls(4).toIntOrNull()
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "not found lotto time")
        val numbersList = mutableListOf<List<Int>>()

        while (queue.size > 12) {
            if (!parameterSeparators.contains(queue.poll())) continue
            val numbers = (1..6).map { queue.polls(2).toIntOrNull() ?: 0 }
            numbersList.add(numbers)
        }
        return numbersList.map { LottoNumberUrlDecodeResponse(times, it) }
    }

    fun findByTimes(times: Int?): Lotto {
        val lotto = if (times != null) lottoRepository.findByIdOrNull(times)
        else lottoRepository.findTopByOrderByTimesDesc()
        return lotto ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "not found lotto")
    }
}