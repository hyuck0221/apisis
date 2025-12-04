package com.hshim.apisis.api.lotto.controller

import com.hshim.apisis.api.lotto.model.LottoDetailSearchCondition
import com.hshim.apisis.api.lotto.model.LottoNumberUrlDecodeResponse
import com.hshim.apisis.api.lotto.model.LottoResponse
import com.hshim.apisis.api.lotto.model.LottoUrlSearchCondition
import com.hshim.apisis.api.lotto.service.LottoQueryService
import com.hshim.apisis.common.annotation.Information
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/lotto")
class LottoController(private val lottoQueryService: LottoQueryService) {
    @Information(
        category = "로또",
        title = "URL로 번호 조회",
        description = "로또 용지에 QR 인식 시 나오는 URL로 회차와 번호를 찾아냅니다",
        version = "1.0",
        callLimit = 200000,
    )
    @GetMapping("/info/by-url")
    fun findLottoInfoByUrl(condition: LottoUrlSearchCondition): ResponseEntity<List<LottoNumberUrlDecodeResponse>> {
        return ResponseEntity.ok(lottoQueryService.findLottoInfoByUrl(condition.url))
    }

    @Information(
        category = "로또",
        title = "결과 조회",
        description = "회차로 로또 결과를 조회합니다",
        version = "1.0",
        callLimit = 500000,
    )
    @GetMapping("/by-times")
    fun findByTimes(condition: LottoDetailSearchCondition): ResponseEntity<LottoResponse> {
        val response = LottoResponse(lottoQueryService.findByTimes(condition.times))
        return ResponseEntity.ok(response)
    }
}