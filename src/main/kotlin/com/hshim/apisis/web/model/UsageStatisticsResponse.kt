package com.hshim.apisis.web.model

data class UsageStatistics(
    val totalCalls: Long,
    val successCalls: Long,
    val failureCalls: Long,
    val successRate: Double,
    val avgResponseTime: Long,
    val period: String,
)

data class ApiStatistics(
    val apiPath: String,
    val method: String,
    val calls: Long,
    val success: Long,
    val failure: Long,
    val successRate: Double,
    val avgResponseTime: Long,
)

data class HourlyTraffic(
    val hour: Int,
    val calls: Long,
)

data class UsageResponse(
    val statistics: UsageStatistics,
    val apiStatistics: List<ApiStatistics>,
    val hourlyTraffic: List<HourlyTraffic>,
)
