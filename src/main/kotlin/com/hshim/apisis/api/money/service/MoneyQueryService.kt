package com.hshim.apisis.api.money.service

import com.hshim.apisis.api.money.enums.CurrencyCode
import com.hshim.apisis.api.money.model.CurrencyCodeResponse
import com.hshim.apisis.api.money.model.ExchangeResponse
import com.hshim.apisis.api.money.model.ExchangeSearchCondition
import com.hshim.apisis.properties.ExchangeProperties
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.client.RestTemplate
import org.springframework.web.server.ResponseStatusException
import java.time.LocalDate
import kotlin.math.roundToInt

@Service
@Transactional
class MoneyQueryService(private val exchangeProperties: ExchangeProperties) {

    private val restTemplate = RestTemplate()
    private val fallbackCnt = 14

    fun exchange(condition: ExchangeSearchCondition): ExchangeResponse {
        val startDate = condition.date?.let { LocalDate.parse(it) } ?: LocalDate.now()

        var rateMap = fetchRateMap(startDate)

        var processCnt = 0L
        while (rateMap.size <= 1) {
            if (processCnt > fallbackCnt) throw ResponseStatusException(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "No exchange rate data available within 7 days"
            )
            rateMap = fetchRateMap(startDate.minusDays(++processCnt))
        }

        val baseCurrencyCode = condition.baseCurrencyCode ?: CurrencyCode.USD
        val baseAmount = condition.baseAmount ?: 1.0
        val baseKrw = baseAmount * (rateMap[baseCurrencyCode]
            ?: throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Unsupported currency code: $baseCurrencyCode"))

        return ExchangeResponse(
            baseCurrencyCode = baseCurrencyCode,
            baseAmount = baseAmount,
            countries = rateMap.map { (code, rate) ->
                ExchangeResponse.CountryDetail(code, (baseKrw / rate * 100.0).roundToInt() / 100.0)
            },
        )
    }

    private fun fetchRateMap(date: LocalDate): Map<CurrencyCode, Double> {
        val body = try {
            restTemplate.exchange(
                "${exchangeProperties.url}?tr_date=$date",
                HttpMethod.GET,
                null,
                String::class.java
            ).body?.trim() ?: return emptyMap()
        } catch (_: Exception) {
            return emptyMap()
        }
        return parseRateMap(body)
    }

    private fun parseRateMap(body: String): Map<CurrencyCode, Double> = buildMap {
        put(CurrencyCode.KRW, 1.0)
        body.splitToSequence("&").forEach { segment ->
            val eq = segment.indexOf('=')
            if (eq < 0) return@forEach
            val code = CurrencyCode.entries.find { it.name == segment.substring(0, eq) } ?: return@forEach
            val rate = segment.substring(eq + 1).replace(",", "").toDoubleOrNull()?.div(code.perUnits) ?: return@forEach
            put(code, rate)
        }
    }

    fun currencyCode(): List<CurrencyCodeResponse> {
        return CurrencyCode.entries.map { CurrencyCodeResponse(it) }
    }
}
