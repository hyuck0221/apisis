package com.hshim.apisis.api.lotto.entity

import com.hshim.apisis.api.base.entity.BaseTimeEntity
import com.hshim.apisis.api.base.entity.converter.ListIntConverter
import jakarta.persistence.*
import java.time.LocalDate


@Entity
@Table(name = "lotto")
class Lotto(
    @Id
    @Column(nullable = false)
    var times: Int,

    @Column(nullable = false)
    var openDate: LocalDate,

    @Column(nullable = false, columnDefinition = "varchar(255)")
    @Convert(converter = ListIntConverter::class)
    var numbers: List<Int>,

    @Column(nullable = false)
    var bonusNumber: Int,

    @Column(nullable = false)
    var totalPrize: Long,

    @Column(nullable = false)
    var firstWinnerPrize: Long,

    @Column(nullable = false)
    var winCnt: Int,
) : BaseTimeEntity()