package com.hshim.apisis.auth.controller

import com.hshim.apisis.auth.model.ApiKeyStatsResponse
import com.hshim.apisis.auth.model.UsageResponse
import com.hshim.apisis.auth.service.ApiKeyStatsService
import com.hshim.apisis.common.annotation.Information
import com.hshim.apisis.user.service.UserUtil.getCurrentUserId
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/auth/keys/stats")
class ApiKeyStatsController(
    private val apiKeyStatsService: ApiKeyStatsService
) {
    @GetMapping
    fun getApiKeyStats(): List<ApiKeyStatsResponse> {
        val userId = getCurrentUserId()
        return apiKeyStatsService.getApiKeyStats(userId)
    }

    @GetMapping("/usage")
    fun getUsageStatistics(
        @RequestParam(defaultValue = "today") period: String,
        @RequestParam(required = false) startDate: String?,
        @RequestParam(required = false) endDate: String?,
    ): ResponseEntity<UsageResponse> {
        val userId = getCurrentUserId()
        val response = apiKeyStatsService.getUsageStatistics(userId, period, startDate, endDate)
        return ResponseEntity.ok(response)
    }
}
