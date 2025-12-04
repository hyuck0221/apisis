package com.hshim.apisis.web.controller

import com.hshim.apisis.web.model.DashboardStatsResponse
import com.hshim.apisis.web.service.StatsQueryService
import com.hshim.apisis.user.service.UserUtil.getCurrentUserId
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/web/stats")
class StatsController(private val statsQueryService: StatsQueryService) {

    @GetMapping
    fun getDashboardStats(): DashboardStatsResponse {
        val userId = getCurrentUserId()
        return statsQueryService.getDashboardStats(userId)
    }
}
