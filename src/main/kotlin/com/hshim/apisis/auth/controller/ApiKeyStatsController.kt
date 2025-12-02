package com.hshim.apisis.auth.controller

import com.hshim.apisis.auth.model.ApiKeyStatsResponse
import com.hshim.apisis.auth.service.ApiKeyStatsService
import com.hshim.apisis.user.service.UserUtil.getCurrentUserId
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
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
}
