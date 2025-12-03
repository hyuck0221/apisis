package com.hshim.apisis.api.escape.model

import com.hshim.apisis.api.escape.entity.EscapeTheme
import com.hshim.apisis.common.annotation.FieldDescription

class EscapeThemeResponse(
    @FieldDescription("테마 참조 ID")
    val refId: Long,

    @FieldDescription("방탈출 카페 ID")
    val escapeCafeId: String,

    @FieldDescription("테마명")
    val name: String,

    @FieldDescription("테마 설명")
    val description: String?,

    @FieldDescription("플레이 시간 (분)")
    val playtime: Int,

    @FieldDescription("운영 여부")
    val isOpen: Boolean,

    @FieldDescription("사진 URL")
    val photoUrl: String,

    @FieldDescription("가격 (원)")
    val price: Int,

    @FieldDescription("난이도 (0-5)")
    val difficulty: Double,

    @FieldDescription("공포도 (0-5)")
    val fear: Double,

    @FieldDescription("활동성 (0-5)")
    val activity: Double,

    @FieldDescription("만족도 (0-5)")
    val satisfy: Double,

    @FieldDescription("문제 수준 (0-5)")
    val problem: Double,

    @FieldDescription("스토리성 (0-5)")
    val story: Double,

    @FieldDescription("인테리어 (0-5)")
    val interior: Double,

    @FieldDescription("연기력 (0-5)")
    val act: Double,
) {
    constructor(escapeTheme: EscapeTheme) : this(
        refId = escapeTheme.refId,
        escapeCafeId = escapeTheme.escapeCafe.id,
        name = escapeTheme.name,
        description = escapeTheme.description,
        isOpen = escapeTheme.isOpen,
        photoUrl = escapeTheme.photoUrl,
        playtime = escapeTheme.playtime,
        price = escapeTheme.price,
        difficulty = escapeTheme.difficulty,
        fear = escapeTheme.fear,
        activity = escapeTheme.activity,
        satisfy = escapeTheme.satisfy,
        problem = escapeTheme.problem,
        story = escapeTheme.story,
        interior = escapeTheme.interior,
        act = escapeTheme.act,
    )
}