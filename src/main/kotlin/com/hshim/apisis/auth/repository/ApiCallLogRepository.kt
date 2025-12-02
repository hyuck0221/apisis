package com.hshim.apisis.auth.repository

import com.hshim.apisis.auth.entity.ApiCallLog
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
}
