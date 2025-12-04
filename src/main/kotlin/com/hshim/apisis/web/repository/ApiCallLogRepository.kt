package com.hshim.apisis.web.repository

import com.hshim.apisis.web.entity.ApiCallLog
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.time.LocalDateTime

@Repository
interface ApiCallLogRepository : JpaRepository<ApiCallLog, String> {
    fun countByApiKeyValueAndUrlAndMethodAndCalledAtAfter(
        apiKeyValue: String,
        url: String,
        method: String,
        calledAt: LocalDateTime = LocalDateTime.now().toLocalDate().atStartOfDay(),
    ): Long

    fun countByApiKeyValue(apiKeyValue: String): Long

    @Query("SELECT COUNT(a) FROM ApiCallLog a WHERE a.apiKeyValue IN :apiKeyValues")
    fun countByApiKeyValueIn(@Param("apiKeyValues") apiKeyValues: List<String>): Long

    @Query("SELECT AVG(a.responseTimeMs) FROM ApiCallLog a WHERE a.apiKeyValue IN :apiKeyValues")
    fun getAverageResponseTimeByApiKeyValueIn(@Param("apiKeyValues") apiKeyValues: List<String>): Double?

    @Query(
        """
        SELECT CAST(SUM(CASE WHEN a.isSuccess = true THEN 1 ELSE 0 END) AS double) / COUNT(a) * 100
        FROM ApiCallLog a
        WHERE a.apiKeyValue IN :apiKeyValues
    """
    )
    fun getSuccessRateByApiKeyValueIn(@Param("apiKeyValues") apiKeyValues: List<String>): Double?

    @Query("SELECT AVG(a.responseTimeMs) FROM ApiCallLog a WHERE a.apiKeyValue = :apiKeyValue")
    fun getAverageResponseTimeByApiKeyValue(@Param("apiKeyValue") apiKeyValue: String): Double?

    @Query(
        """
        SELECT CAST(SUM(CASE WHEN a.isSuccess = true THEN 1 ELSE 0 END) AS double) / COUNT(a) * 100
        FROM ApiCallLog a
        WHERE a.apiKeyValue = :apiKeyValue
    """
    )
    fun getSuccessRateByApiKeyValue(@Param("apiKeyValue") apiKeyValue: String): Double?

    @Query("SELECT COUNT(DISTINCT a.url) FROM ApiCallLog a WHERE a.apiKeyValue = :apiKeyValue")
    fun countDistinctUrlByApiKeyValue(@Param("apiKeyValue") apiKeyValue: String): Long

    @Query("SELECT COUNT(DISTINCT a.url) FROM ApiCallLog a WHERE a.apiKeyValue IN :apiKeyValues")
    fun countDistinctUrlByApiKeyValueIn(@Param("apiKeyValues") apiKeyValues: List<String>): Long

    fun deleteAllByApiKeyValue(apiKeyValue: String)

    // 사용량 통계를 위한 쿼리
    @Query("""
        SELECT a FROM ApiCallLog a
        WHERE a.apiKeyValue IN (
            SELECT k.keyValue FROM ApiKey k WHERE k.user.id = :userId
        )
        AND a.calledAt BETWEEN :startTime AND :endTime
    """)
    fun findAllByUserIdAndCalledAtBetween(
        @Param("userId") userId: String,
        @Param("startTime") startTime: LocalDateTime,
        @Param("endTime") endTime: LocalDateTime
    ): List<ApiCallLog>
}
