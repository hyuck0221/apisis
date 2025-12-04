package com.hshim.apisis.web.service

import com.hshim.apisis.web.model.DashboardStatsResponse
import com.hshim.apisis.web.repository.ApiCallLogRepository
import com.hshim.apisis.web.repository.ApiKeyRepository
import org.springframework.stereotype.Service
import kotlin.math.roundToLong

@Service
class StatsQueryService(
    private val apiKeyRepository: ApiKeyRepository,
    private val apiCallLogRepository: ApiCallLogRepository
) {

    fun getDashboardStats(userId: String): DashboardStatsResponse {
        val apiKeys = apiKeyRepository.findAllByUserId(userId)
        if (apiKeys.isEmpty()) return DashboardStatsResponse()

        val apiKeyValues = apiKeys.map { it.keyValue }

        val totalApiCalls =
            apiCallLogRepository.countByApiKeyValueIn(apiKeyValues)
        val averageResponseTime =
            apiCallLogRepository.getAverageResponseTimeByApiKeyValueIn(apiKeyValues)?.roundToLong() ?: 0L
        val successRate =
            apiCallLogRepository.getSuccessRateByApiKeyValueIn(apiKeyValues) ?: 100.0

        return DashboardStatsResponse(
            apiKeyCount = apiKeys.size.toLong(),
            totalApiCalls = totalApiCalls,
            averageResponseTimeMs = averageResponseTime,
            successRate = successRate
        )
    }
}
