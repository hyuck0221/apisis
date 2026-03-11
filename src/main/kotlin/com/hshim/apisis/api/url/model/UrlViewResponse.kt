package com.hshim.apisis.api.url.model

import com.hshim.apisis.api.url.entity.UrlShort
import com.hshim.apisis.common.annotation.FieldDescription
import util.DateUtil.dateToString

data class UrlViewResponse(
    @FieldDescription("조회 수")
    val view: Long,
    @FieldDescription("최근 조회 일자")
    val lastViewDate: String,
) {
    constructor(urlShort: UrlShort) : this(
        view = urlShort.view,
        lastViewDate = urlShort.updateDate.dateToString()
    )
}