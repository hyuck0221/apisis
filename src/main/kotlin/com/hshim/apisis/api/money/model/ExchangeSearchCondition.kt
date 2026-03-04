package com.hshim.apisis.api.money.model

import com.hshim.apisis.api.money.enums.CurrencyCode
import com.hshim.apisis.common.annotation.FieldDescription
import org.springframework.http.HttpStatus
import org.springframework.web.server.ResponseStatusException
import util.DateUtil.stringToDate
import java.time.LocalDate

data class ExchangeSearchCondition (
    @FieldDescription("기준 통화 코드 (default: USD)")
    val baseCurrencyCode: CurrencyCode?,
    @FieldDescription("기준 금액 (default: 1.0)")
    val baseAmount: Double?,
    @FieldDescription("날짜 (default: 오늘, format: yyyy-MM-dd, min:1990-03-02)")
    val date: String?,
) {
    init {
        when {
            baseAmount != null && baseAmount <= 0 -> throw ResponseStatusException(HttpStatus.BAD_REQUEST, "amount too smaller")
            date != null && date.stringToDate<LocalDate>() < LocalDate.of(1990, 3, 2) ->
                throw ResponseStatusException(HttpStatus.BAD_REQUEST, "date too past")
        }
    }
}