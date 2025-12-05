package com.hshim.apisis.web.entity

import com.hshim.apisis.api.base.entity.BaseTimeEntity
import com.hshim.apisis.user.entity.User
import jakarta.persistence.*
import util.CommonUtil.ulid
import java.time.LocalDate

@Entity
@Table(name = "analytics")
class Analytics(
    @Id
    val id: String = ulid(),

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    val user: User,

    @Column(nullable = false, columnDefinition = "LONGTEXT")
    var html: String,

    @Column(nullable = false)
    var searchStartDate: LocalDate,

    @Column(nullable = false)
    var searchEndDate: LocalDate,

) : BaseTimeEntity() {
    constructor(
        userId: String,
        html: String,
        searchStartDate: LocalDate,
        searchEndDate: LocalDate,
    ) : this(
        user = User.of(userId),
        html = html,
        searchStartDate = searchStartDate,
        searchEndDate = searchEndDate,
    )
}