package com.hshim.apisis.api.money.model

import com.hshim.apisis.api.money.enums.CurrencyCode
import com.hshim.apisis.common.annotation.FieldDescription

data class CurrencyCodeResponse(
    @FieldDescription("기준 통화 코드")
    val currencyCode: CurrencyCode,
    @FieldDescription("국가이름 (한글)")
    val countryName: String,
    @FieldDescription("단위 (한글)")
    val unit: String,
    @FieldDescription("심볼")
    val symbol: String,
) {
    constructor(currencyCode: CurrencyCode) : this(
        currencyCode = currencyCode,
        countryName = currencyCode.country,
        unit = currencyCode.unit,
        symbol = currencyCode.symbol,
    )
}