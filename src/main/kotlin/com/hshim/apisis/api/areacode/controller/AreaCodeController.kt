package com.hshim.apisis.api.areacode.controller

import com.hshim.apisis.api.areacode.model.AreaCodeCondition
import com.hshim.apisis.api.areacode.model.AreaCodeResponse
import com.hshim.apisis.api.areacode.service.AreaCodeQueryService
import com.hshim.apisis.common.annotation.Information
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/area-codes")
class AreaCodeController(private val areaCodeQueryService: AreaCodeQueryService) {

    @Information(
        category = "법정동코드",
        title = "법정동코드 내역 검색",
        description = "법정동 코드 내역을 검색합니다",
        version = "1.0",
        callLimitFree = 500,
        callLimitBasic = 1500,
        callLimitPro = 500000,
    )
    @GetMapping
    fun findAllBy(
        condition: AreaCodeCondition,
        pageable: Pageable,
    ): ResponseEntity<Page<AreaCodeResponse>> {
        val result = areaCodeQueryService.findAllPageBy(condition, pageable).map { AreaCodeResponse(it) }
        return ResponseEntity.ok(result)
    }

}
