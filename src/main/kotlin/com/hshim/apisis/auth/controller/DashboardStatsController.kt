package com.hshim.apisis.auth.controller

import com.hshim.apisis.auth.model.DashboardStatsResponse
import com.hshim.apisis.auth.service.DashboardStatsService
import com.hshim.apisis.user.service.UserUtil.getCurrentUserIdOrNull
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/dashboard")
class DashboardStatsController(
    private val dashboardStatsService: DashboardStatsService
) {

    @GetMapping("/stats")
    fun getDashboardStats(): ResponseEntity<DashboardStatsResponse> {
        val userId = getCurrentUserIdOrNull()
            ?: return ResponseEntity.status(401).build()

        val stats = dashboardStatsService.getDashboardStats(userId)
        return ResponseEntity.ok(stats)
    }
}
