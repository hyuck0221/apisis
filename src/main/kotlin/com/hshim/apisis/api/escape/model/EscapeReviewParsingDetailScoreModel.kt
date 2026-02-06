package com.hshim.apisis.api.escape.model

import io.clroot.excel.annotation.Column
import io.clroot.excel.annotation.Excel

@Excel
data class EscapeReviewParsingDetailScoreModel (
    @Column("번호") val no: String?,
    @Column("지역") val area: String?,
    @Column("지점명") val cafeName: String?,
    @Column("테마명") val themeName: String?,
    @Column("평점") val average: String?,
    @Column("문제") val problem: String?,
    @Column("인테리어") val interior: String?,
    @Column("스토리") val story: String?,
    @Column("창의성") val idea: String?,
    @Column("연출") val act: String?,
)