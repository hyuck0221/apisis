package com.hshim.apisis.api.escape.model

import com.hshim.apisis.common.annotation.FieldDescription

class EscapeCafeSearchCondition(
    @FieldDescription("검색어")
    val search: String?,

    @FieldDescription("영업중만 표시 여부")
    val onlyOpen: Boolean?,

    @FieldDescription("지역 (예: 서울)")
    val area: String?,

    @FieldDescription("구역 (예: 홍대)")
    val location: String?,

)