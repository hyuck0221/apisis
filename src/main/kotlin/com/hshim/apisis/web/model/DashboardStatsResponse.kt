package com.hshim.apisis.web.model

data class DashboardStatsResponse(
    val apiKeyCount: Long = 0,
    val totalApiCalls: Long = 0,
    val averageResponseTimeMs: Long = 0,
    val successRate: Double = 100.0
)
