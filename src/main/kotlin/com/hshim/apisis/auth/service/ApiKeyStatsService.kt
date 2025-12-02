package com.hshim.apisis.auth.service

import com.hshim.apisis.auth.model.ApiKeyStatsResponse
import com.hshim.apisis.auth.model.ApiStatistics
import com.hshim.apisis.auth.model.HourlyTraffic
import com.hshim.apisis.auth.model.UsageResponse
import com.hshim.apisis.auth.model.UsageStatistics
import com.hshim.apisis.auth.repository.ApiCallLogRepository
import com.hshim.apisis.auth.repository.ApiKeyRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime
import kotlin.collections.component1
import kotlin.collections.component2
import kotlin.math.roundToLong

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

    fun getUsageStatistics(
        userId: String,
        period: String,
        startDate: String? = null,
        endDate: String? = null
    ): UsageResponse {
        val (startTime, endTime) = getPeriodRange(period, startDate, endDate)

        // apiCallLog 테이블에서 사용자의 모든 API 키에 대한 로그 조회
        val callLogs = apiCallLogRepository.findAllByUserIdAndCalledAtBetween(userId, startTime, endTime)

        val totalCalls = callLogs.size.toLong()
        val successCalls = callLogs.count { it.isSuccess }.toLong()
        val failureCalls = totalCalls - successCalls
        val successRate = if (totalCalls > 0) (successCalls.toDouble() / totalCalls * 100) else 0.0
        val avgResponseTime = if (callLogs.isNotEmpty()) {
            callLogs.map { it.responseTimeMs }.average().roundToLong()
        } else 0L

        val statistics = UsageStatistics(
            totalCalls = totalCalls,
            successCalls = successCalls,
            failureCalls = failureCalls,
            successRate = successRate,
            avgResponseTime = avgResponseTime,
            period = period,
        )

        // API별 통계
        val apiStatistics = callLogs
            .groupBy { "${it.method}:${it.url}" }
            .map { (key, apiCalls) ->
                val method = key.substringBefore(":")
                val path = key.substringAfter(":")
                val calls = apiCalls.size.toLong()
                val success = apiCalls.count { it.isSuccess }.toLong()
                val failure = calls - success
                val rate = if (calls > 0) (success.toDouble() / calls * 100) else 0.0
                val avgTime = if (apiCalls.isNotEmpty()) {
                    apiCalls.map { it.responseTimeMs }.average().roundToLong()
                } else 0L

                ApiStatistics(
                    apiPath = path,
                    method = method,
                    calls = calls,
                    success = success,
                    failure = failure,
                    successRate = rate,
                    avgResponseTime = avgTime,
                )
            }
            .sortedByDescending { it.calls }

        // 시간대별 트래픽
        val hourlyTraffic = callLogs
            .groupBy { it.calledAt.hour }
            .map { (hour, hourCalls) ->
                HourlyTraffic(
                    hour = hour,
                    calls = hourCalls.size.toLong(),
                )
            }
            .sortedBy { it.hour }

        return UsageResponse(
            statistics = statistics,
            apiStatistics = apiStatistics,
            hourlyTraffic = hourlyTraffic,
        )
    }

    private fun getPeriodRange(
        period: String,
        startDate: String?,
        endDate: String?
    ): Pair<LocalDateTime, LocalDateTime> {
        val now = LocalDateTime.now()

        return when (period) {
            "today" -> Pair(now.toLocalDate().atStartOfDay(), now)
            "week" -> Pair(now.minusDays(7), now)
            "month" -> Pair(now.minusDays(30), now)
            "custom" -> {
                if (startDate != null && endDate != null) {
                    val start = java.time.LocalDate.parse(startDate).atStartOfDay()
                    val end = java.time.LocalDate.parse(endDate).atTime(23, 59, 59)
                    Pair(start, end)
                } else {
                    Pair(now.toLocalDate().atStartOfDay(), now)
                }
            }
            else -> Pair(now.toLocalDate().atStartOfDay(), now)
        }
    }
}
