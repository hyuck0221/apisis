package com.hshim.apisis.auth.controller

import com.hshim.apisis.auth.model.DashboardStatsResponse
import com.hshim.apisis.auth.service.StatsService
import com.hshim.apisis.user.service.UserUtil.getCurrentUserId
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/auth/stats")
class StatsController(private val statsService: StatsService) {

    @GetMapping
    fun getDashboardStats(): DashboardStatsResponse {
        val userId = getCurrentUserId()
        return statsService.getDashboardStats(userId)
    }
}
