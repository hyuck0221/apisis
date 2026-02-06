package com.hshim.apisis.api.escape.model

import io.clroot.excel.annotation.Column
import io.clroot.excel.annotation.Excel

@Excel
data class EscapeOneLineReviewModel (
    @Column("번호") val no: String?,
    @Column("지역") val area: String?,
    @Column("지점명") val cafeName: String?,
    @Column("테마명") val themeName: String?,
    @Column("난이도") val difficulty: String?,
    @Column("추천도") val satisfy: String?,
    @Column("한줄평") val review: String?,
    @Column("탈출 팁!") val escapeTip: String?,
)