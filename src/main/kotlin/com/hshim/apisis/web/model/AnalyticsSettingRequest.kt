package com.hshim.apisis.web.model

import com.hshim.apisis.user.entity.User
import com.hshim.apisis.web.entity.AnalyticsSetting
import com.hshim.apisis.web.enums.AnalyticsRange

data class AnalyticsSettingRequest(
    val enabled: Boolean,
    val range: AnalyticsRange,
) {
    fun toEntity(userId: String) = AnalyticsSetting(
        enabled = enabled,
        dateRange = range,
        user = User.of(userId),
        nextAnalyticsDate = range.toAnalyticsDate(),
    )

    fun updateTo(setting: AnalyticsSetting) {
        setting.enabled = enabled
        setting.dateRange = range
        setting.nextAnalyticsDate = range.toAnalyticsDate()
    }
}