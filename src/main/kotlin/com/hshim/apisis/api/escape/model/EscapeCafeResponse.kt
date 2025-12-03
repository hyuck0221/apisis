package com.hshim.apisis.api.escape.model

import com.hshim.apisis.api.escape.entity.EscapeCafe
import com.hshim.apisis.api.escape.entity.EscapeTheme
import com.hshim.apisis.common.annotation.FieldDescription
import util.DateUtil.dateToString

class EscapeCafeResponse(
    @FieldDescription("카페 ID")
    val id: String,

    @FieldDescription("카페명")
    val name: String,

    @FieldDescription("지역 (예: 서울)")
    val location: String,

    @FieldDescription("구역 (예: 강남구)")
    val area: String,

    @FieldDescription("영업 여부")
    val isOpen: Boolean,

    @FieldDescription("위도")
    val lat: Double,

    @FieldDescription("경도")
    val lng: Double,

    @FieldDescription("주소")
    val address: String,

    @FieldDescription("전화번호")
    val phoneNo: String,

    @FieldDescription("홈페이지")
    val homepage: String,

    @FieldDescription("카페 테마 목록")
    val themes: List<EscapeThemeResponse>,

    @FieldDescription("생성일")
    val createDate: String,

    @FieldDescription("수정일")
    val updateDate: String,
) {
    constructor(escapeCafe: EscapeCafe) : this(
        id = escapeCafe.id,
        name = escapeCafe.name,
        location = escapeCafe.location,
        area = escapeCafe.area,
        isOpen = escapeCafe.isOpen,
        lat = escapeCafe.lat,
        lng = escapeCafe.lng,
        address = escapeCafe.address,
        phoneNo = escapeCafe.phoneNo,
        homepage = escapeCafe.homepage,
        themes = emptyList(),
        createDate = escapeCafe.createDate.dateToString(),
        updateDate = escapeCafe.updateDate.dateToString(),
    )

    constructor(escapeCafe: EscapeCafe, escapeThemes: List<EscapeTheme>) : this(
        id = escapeCafe.id,
        name = escapeCafe.name,
        location = escapeCafe.location,
        area = escapeCafe.area,
        isOpen = escapeCafe.isOpen,
        lat = escapeCafe.lat,
        lng = escapeCafe.lng,
        address = escapeCafe.address,
        phoneNo = escapeCafe.phoneNo,
        homepage = escapeCafe.homepage,
        themes = escapeThemes.map { EscapeThemeResponse(it) },
        createDate = escapeCafe.createDate.dateToString(),
        updateDate = escapeCafe.updateDate.dateToString(),
    )
}