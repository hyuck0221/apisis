package com.hshim.apisis.auth.service

import com.hshim.apisis.auth.model.DashboardStatsResponse
import com.hshim.apisis.auth.repository.ApiCallLogRepository
import com.hshim.apisis.auth.repository.ApiKeyRepository
import org.springframework.stereotype.Service
import kotlin.math.roundToLong

@Service
class DashboardStatsService(
    private val apiKeyRepository: ApiKeyRepository,
    private val apiCallLogRepository: ApiCallLogRepository
) {

    fun getDashboardStats(userId: String): DashboardStatsResponse {
        val apiKeys = apiKeyRepository.findAllByUserId(userId)
        val apiKeyCount = apiKeys.size.toLong()

        if (apiKeys.isEmpty()) {
            return DashboardStatsResponse(
                apiKeyCount = 0,
                totalApiCalls = 0,
                averageResponseTimeMs = 0,
                successRate = 100.0
            )
        }

        val apiKeyValues = apiKeys.map { it.keyValue }

        val totalApiCalls = apiCallLogRepository.countByApiKeyValueIn(apiKeyValues)
        val averageResponseTime = apiCallLogRepository.getAverageResponseTimeByApiKeyValueIn(apiKeyValues)?.roundToLong() ?: 0L
        val successRate = apiCallLogRepository.getSuccessRateByApiKeyValueIn(apiKeyValues) ?: 100.0

        return DashboardStatsResponse(
            apiKeyCount = apiKeyCount,
            totalApiCalls = totalApiCalls,
            averageResponseTimeMs = averageResponseTime,
            successRate = successRate
        )
    }
}
