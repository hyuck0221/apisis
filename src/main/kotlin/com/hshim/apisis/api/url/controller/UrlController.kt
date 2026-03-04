package com.hshim.apisis.api.url.controller

import com.hshim.apisis.api.url.model.QRRequest
import com.hshim.apisis.api.url.model.QrResponse
import com.hshim.apisis.api.url.model.UrlRequest
import com.hshim.apisis.api.url.model.UrlResponse
import com.hshim.apisis.api.url.service.UrlQueryService
import com.hshim.apisis.common.annotation.Information
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/url")
class UrlController(private val urlQueryService: UrlQueryService) {
    @Information(
        category = "URL",
        title = "단축 url 생성",
        description = "단축된 url을 생성합니다",
        version = "1.0",
        callLimitFree = 100,
        callLimitBasic = 2000,
        callLimitPro = 500000
    )
    @PostMapping("/short")
    fun urlShorter(@RequestBody request: UrlRequest): ResponseEntity<UrlResponse> {
        return ResponseEntity.ok(urlQueryService.urlShorter(request))
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
        return ResponseEntity.ok(urlQueryService.generateQR(request))
    }
}