package com.hshim.apisis.api.escape.model

import io.clroot.excel.annotation.Column
import io.clroot.excel.annotation.Excel

@Excel
data class EscapeReviewRecommendedModel(
    @Column("번호") val no: String?,
    @Column("지역") val area: String?,
    @Column("지점명") val cafeName: String?,
    @Column("호점") val locationDetail: String?,
    @Column("테마명") val themeName: String?,
    @Column("난이도") val difficulty: String?,
    @Column("추천도") val satisfy: String?,
    @Column("공포도") val fear: String?,
    @Column("활동성") val activity: String?,
) {
    fun toRequest() = EscapeReviewRequest(
        no = no ?: "",
        cafeName = cafeName ?: "",
        themeName = themeName ?: "",
        difficulty = difficulty,
        satisfy = satisfy,
        fear = fear?.substring(1)?.toInt(),
        activity = activity?.substring(1)?.toInt(),
    )
}