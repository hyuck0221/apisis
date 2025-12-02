package com.hshim.apisis.auth.model

data class ApiKeyStatsResponse(
    val apiKeyValue: String,
    val name: String,
    val active: Boolean,
    val totalCalls: Long,
    val successRate: Double,
    val averageResponseTimeMs: Long,
    val uniqueApiCount: Long,
    val totalCallsAllKeys: Long,
    val totalUniqueApisAllKeys: Long
)
