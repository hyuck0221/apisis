package com.hshim.apisis.api.escape.model

import com.hshim.apisis.common.annotation.FieldDescription

class EscapeCafeSearchCondition(
    @FieldDescription("검색어 (카페명)")
    val search: String?,

    @FieldDescription("영업중인 카페만 조회")
    val onlyOpen: Boolean?,

    @FieldDescription("지역 (예: 서울)")
    val location: String?,

    @FieldDescription("구역 (예: 강남구)")
    val area: String?,
)