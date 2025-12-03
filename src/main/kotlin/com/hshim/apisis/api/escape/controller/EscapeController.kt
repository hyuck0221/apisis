package com.hshim.apisis.api.escape.controller

import com.hshim.apisis.api.escape.model.*
import com.hshim.apisis.api.escape.service.EscapeCafeQueryService
import com.hshim.apisis.api.escape.service.EscapeThemeQueryService
import com.hshim.apisis.common.annotation.Information
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/escape")
class EscapeController(
    private val escapeCafeQueryService: EscapeCafeQueryService,
    private val escapeThemeQueryService: EscapeThemeQueryService,
) {
    @Information(
        category = "방탈출",
        title = "카페 지역 검색",
        description = "전국의 방탈출 카페가 있는 지역을 검색합니다",
        version = "1.0",
    )
    @GetMapping("/cafes/location")
    fun findAllCafesLocation(): ResponseEntity<List<EscapeCafeLocationResponse>> {
        return ResponseEntity.ok(escapeCafeQueryService.findAllCafesLocation())
    }

    @Information(
        category = "방탈출",
        title = "카페 검색",
        description = "전국의 방탈출 카페를 검색합니다",
        version = "1.0",
    )
    @GetMapping("/cafes")
    fun findAllCafes(
        condition: EscapeCafeSearchCondition,
        pageable: Pageable,
    ): ResponseEntity<Page<EscapeCafeResponse>> {
        val cafePage = escapeCafeQueryService.findAllPageBy(condition, pageable)
        val themesMap = escapeThemeQueryService.findAllByCafes(cafePage.content)
            .groupBy { it.escapeCafe.id }

        val response = cafePage.map { EscapeCafeResponse(it, themesMap[it.id].orEmpty()) }
        return ResponseEntity.ok(response)
    }

    @Information(
        category = "방탈출",
        title = "카페 위도/경도 검색",
        description = "전국의 방탈출 카페를 위도/경도로 검색합니다",
        version = "1.0",
    )
    @GetMapping("/cafes/by-bounds")
    fun findAllCafesByBounds(condition: EscapeCafeBoundsSearchCondition): ResponseEntity<List<EscapeCafeResponse>> {
        val cafes = escapeCafeQueryService.findAllByBounds(condition)
        val themesMap = escapeThemeQueryService.findAllByCafes(cafes)
            .groupBy { it.escapeCafe.id }

        val response = cafes.map { EscapeCafeResponse(it, themesMap[it.id].orEmpty()) }
        return ResponseEntity.ok(response)
    }

    @Information(
        category = "방탈출",
        title = "테마 검색",
        description = "전국의 방탈출 카페 테마를 검색합니다",
        version = "1.0",
    )
    @GetMapping("/themes")
    fun findAllThemes(
        condition: EscapeThemeSearchCondition,
        pageable: Pageable,
    ): ResponseEntity<Page<EscapeThemeResponse>> {
        val response = escapeThemeQueryService.findAllPageBy(condition, pageable).map { EscapeThemeResponse(it) }
        return ResponseEntity.ok(response)
    }
}
