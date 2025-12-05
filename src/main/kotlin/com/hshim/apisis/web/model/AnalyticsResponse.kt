package com.hshim.apisis.web.model

import com.hshim.apisis.web.entity.Analytics
import util.DateUtil.dateToString

class AnalyticsResponse(
    val id: String,
    val searchStartDate: String,
    val searchEndDate: String,
) {
    constructor(analytics: Analytics) : this(
        id = analytics.id,
        searchStartDate = analytics.searchStartDate.dateToString(),
        searchEndDate = analytics.searchEndDate.dateToString(),
    )
}