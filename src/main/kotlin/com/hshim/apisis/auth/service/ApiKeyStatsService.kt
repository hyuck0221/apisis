package com.hshim.apisis.auth.service

import com.hshim.apisis.auth.model.ApiKeyStatsResponse
import com.hshim.apisis.auth.repository.ApiCallLogRepository
import com.hshim.apisis.auth.repository.ApiKeyRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class ApiKeyStatsService(
    private val apiKeyRepository: ApiKeyRepository,
    private val apiCallLogRepository: ApiCallLogRepository
) {
    fun getApiKeyStats(userId: String): List<ApiKeyStatsResponse> {
        val apiKeys = apiKeyRepository.findAllByUserId(userId)

        if (apiKeys.isEmpty()) {
            return emptyList()
        }

        val allKeyValues = apiKeys.map { it.keyValue }
        val totalCallsAllKeys = apiCallLogRepository.countByApiKeyValueIn(allKeyValues)
        val totalUniqueApisAllKeys = apiCallLogRepository.countDistinctUrlByApiKeyValueIn(allKeyValues)

        return apiKeys.map { apiKey ->
            val totalCalls = apiCallLogRepository.countByApiKeyValue(apiKey.keyValue)
            val successRate = apiCallLogRepository.getSuccessRateByApiKeyValue(apiKey.keyValue) ?: 100.0
            val avgResponseTime = apiCallLogRepository.getAverageResponseTimeByApiKeyValue(apiKey.keyValue)?.toLong() ?: 0L
            val uniqueApiCount = apiCallLogRepository.countDistinctUrlByApiKeyValue(apiKey.keyValue)

            ApiKeyStatsResponse(
                apiKeyValue = apiKey.keyValue,
                name = apiKey.name,
                active = apiKey.isActive,
                totalCalls = totalCalls,
                successRate = successRate,
                averageResponseTimeMs = avgResponseTime,
                uniqueApiCount = uniqueApiCount,
                totalCallsAllKeys = totalCallsAllKeys,
                totalUniqueApisAllKeys = totalUniqueApisAllKeys
            )
        }
    }
}
