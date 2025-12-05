package com.hshim.apisis.web.controller

import com.hshim.apisis.user.service.UserUtil
import com.hshim.apisis.web.model.AnalyticsResponse
import com.hshim.apisis.web.service.AnalyticsCommandService
import com.hshim.apisis.web.service.AnalyticsQueryService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/web/analytics")
class AnalyticsController(
    private val analyticsQueryService: AnalyticsQueryService,
    private val analyticsCommandService: AnalyticsCommandService
) {
    @GetMapping
    fun findAllBy(): List<AnalyticsResponse> {
        val userId = UserUtil.getCurrentUserId()
        return analyticsQueryService.findAllBy(userId)
    }

    @GetMapping("/{id}/html")
    fun findAllBy(@PathVariable id: String): ResponseEntity<String> {
        return ResponseEntity.ok()
            .header("Content-Type", "text/html; charset=UTF-8")
            .body(analyticsQueryService.findHtmlById(id))
    }

    @DeleteMapping("/{id}")
    fun delete(@PathVariable id: String) {
        analyticsCommandService.delete(id)
    }

    @DeleteMapping
    fun deleteAll() {
        val userId = UserUtil.getCurrentUserId()
        analyticsCommandService.deleteAllByUserId(userId)
    }
}
