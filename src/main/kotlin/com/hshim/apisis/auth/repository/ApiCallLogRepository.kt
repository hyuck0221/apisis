package com.hshim.apisis.auth.repository

import com.hshim.apisis.auth.entity.ApiCallLog
import org.springframework.data.jpa.repository.JpaRepository
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
}
