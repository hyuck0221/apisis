package com.hshim.apisis.api.money.controller

import com.hshim.apisis.api.money.model.CurrencyCodeResponse
import com.hshim.apisis.api.money.model.ExchangeResponse
import com.hshim.apisis.api.money.model.ExchangeSearchCondition
import com.hshim.apisis.api.money.service.MoneyQueryService
import com.hshim.apisis.common.annotation.Information
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/money")
class MoneyController(private val moneyQueryService: MoneyQueryService) {
    @Information(
        category = "돈",
        title = "환율 조회",
        description = "환율을 조회합니다",
        version = "1.0",
        callLimitFree = 500,
        callLimitBasic = 10000,
        callLimitPro = 2500000
    )
    @GetMapping("/exchange")
    fun exchange(condition: ExchangeSearchCondition): ResponseEntity<ExchangeResponse> {
        return ResponseEntity.ok(moneyQueryService.exchange(condition))
    }

    @Information(
        category = "돈",
        title = "통화코드 조회",
        description = "지원하는 통화코드 내역을 조회합니다",
        version = "1.0",
        callLimitFree = 1000,
        callLimitBasic = 20000,
        callLimitPro = 5000000
    )
    @GetMapping("/exchange/currency-code")
    fun currencyCode(): ResponseEntity<List<CurrencyCodeResponse>> {
        return ResponseEntity.ok(moneyQueryService.currencyCode())
    }
}