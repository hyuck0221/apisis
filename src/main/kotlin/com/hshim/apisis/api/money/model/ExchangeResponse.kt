package com.hshim.apisis.api.money.model

import com.hshim.apisis.api.money.enums.CurrencyCode
import com.hshim.apisis.common.annotation.FieldDescription

data class ExchangeResponse (
    @FieldDescription("기준 통화 코드")
    val baseCurrencyCode: CurrencyCode,
    @FieldDescription("기준 금액")
    val baseAmount: Double,
    @FieldDescription("국가별 금액")
    val countries: List<CountryDetail>,
) {
    data class CountryDetail(
        val currencyCode: CurrencyCode,
        val countryName: String,
        val unit: String,
        val symbol: String,
        val amount: Double,
    ) {
        constructor(currencyCode: CurrencyCode, amount: Double): this (
            currencyCode = currencyCode,
            countryName = currencyCode.country,
            unit = currencyCode.unit,
            symbol = currencyCode.symbol,
            amount = amount,
        )
    }
}