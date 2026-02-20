package com.hshim.apisis.api.areacode.model

import com.hshim.apisis.common.annotation.FieldDescription

data class AreaCodeCondition (
    @FieldDescription("코드")
    val code: String?,

    @FieldDescription("주소")
    val address: String?,

    @FieldDescription("시")
    val city: String?,

    @FieldDescription("군")
    val town: String?,

    @FieldDescription("구")
    val village: String?,
)