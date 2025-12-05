package com.hshim.apisis.web.controller

import com.hshim.apisis.user.service.UserUtil
import com.hshim.apisis.web.model.AnalyticsSettingRequest
import com.hshim.apisis.web.service.AnalyticsSettingCommandService
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/web/analytics-setting")
class AnalyticsSettingController(
    private val analyticsSettingCommandService: AnalyticsSettingCommandService,
) {
    @PutMapping
    fun update(@RequestBody request: AnalyticsSettingRequest) {
        val userId = UserUtil.getCurrentUserId()
        analyticsSettingCommandService.update(userId, request)
    }

    @PutMapping("/request")
    fun request() {
        val userId = UserUtil.getCurrentUserId()
        analyticsSettingCommandService.request(userId)
    }
}
