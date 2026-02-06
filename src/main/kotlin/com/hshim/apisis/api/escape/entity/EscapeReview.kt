package com.hshim.apisis.api.escape.entity

import com.hshim.apisis.api.base.entity.BaseTimeEntity
import jakarta.persistence.*
import util.CommonUtil.ulid

@Entity
@Table(name = "escape_review")
data class EscapeReview(
    @Id
    val id: String = ulid(),

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "escape_theme")
    var escapeTheme: EscapeTheme,

    @Column(nullable = true)
    var average: Double?,

    @Column(nullable = true)
    var difficulty: String?,

    @Column(nullable = true)
    var satisfy: String?,

    @Column(nullable = true)
    var fear: Int?,

    @Column(nullable = true)
    var activity: Int?,

    @Column(nullable = true)
    var problem: Int?,

    @Column(nullable = true)
    var interior: Int?,

    @Column(nullable = true)
    var story: Int?,

    @Column(nullable = true)
    var idea: Int?,

    @Column(nullable = true)
    var act: Int?,

    @Column(nullable = true)
    var review: String?,

    @Column(nullable = true)
    var escapeTip: String?,

    ) : BaseTimeEntity()