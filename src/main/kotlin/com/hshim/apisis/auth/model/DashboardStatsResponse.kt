package com.hshim.apisis.auth.model

data class DashboardStatsResponse(
    val apiKeyCount: Long,
    val totalApiCalls: Long,
    val averageResponseTimeMs: Long,
    val successRate: Double
)
