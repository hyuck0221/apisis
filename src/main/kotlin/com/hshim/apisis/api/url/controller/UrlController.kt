package com.hshim.apisis.api.url.controller

import com.hshim.apisis.api.url.model.*
import com.hshim.apisis.api.url.service.UrlCommandService
import com.hshim.apisis.api.url.service.UrlQueryService
import com.hshim.apisis.common.annotation.Information
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/url")
class UrlController(
    private val urlQueryService: UrlQueryService,
    private val urlCommandService: UrlCommandService,
) {
    @Information(
        category = "URL",
        title = "단축 URL 생성 (LRL)",
        description = "단축된 URL을 생성합니다 (lrl.kr)",
        version = "1.0",
        callLimitFree = 100,
        callLimitBasic = 2000,
        callLimitPro = 500000
    )
    @PostMapping("/short")
    fun urlShorter(@RequestBody request: UrlRequest): ResponseEntity<UrlResponse> {
        return ResponseEntity.ok(urlCommandService.urlShorter(request))
    }

    @Information(
        category = "URL",
        title = "단축 URL 생성 (apisis)",
        description = "단축된 URL을 생성합니다 (apisis.dev/url)",
        version = "1.0",
        callLimitFree = 100,
        callLimitBasic = 2000,
        callLimitPro = 500000
    )
    @PostMapping("/short/apisis")
    fun urlShorterByAPIsis(@RequestBody request: UrlRequest): ResponseEntity<UrlResponse> {
        return ResponseEntity.ok(urlCommandService.urlShorterByAPIsis(request))
    }

    @Information(
        category = "URL",
        title = "단축 URL 클릭수 조회 (apisis)",
        description = "apisis 단축 URL의 클릭수를 조회합니다",
        version = "1.0",
        callLimitFree = 1000,
        callLimitBasic = 20000,
        callLimitPro = 5000000
    )
    @GetMapping("/short/apisis/{id}")
    fun urlViewCntByAPIsis(@PathVariable id: String): ResponseEntity<UrlViewResponse> {
        return ResponseEntity.ok(urlQueryService.urlViewCntByAPIsis(id))
    }

    @Information(
        category = "URL",
        title = "QR 생성",
        description = "QR을 생성합니다",
        version = "1.0",
        callLimitFree = 100,
        callLimitBasic = 2000,
        callLimitPro = 500000
    )
    @PostMapping("/qr")
    fun generateQR(@RequestBody request: QRRequest): ResponseEntity<QrResponse> {
        return ResponseEntity.ok(urlCommandService.generateQR(request))
    }
}