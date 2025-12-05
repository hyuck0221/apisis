package com.hshim.apisis.web.entity

import com.hshim.apisis.api.base.entity.BaseTimeEntity
import com.hshim.apisis.user.entity.User
import com.hshim.apisis.web.enums.AnalyticsRange
import jakarta.persistence.*
import util.CommonUtil.ulid
import java.time.LocalDate

@Entity
@Table(
    name = "analytics_setting",
    indexes = [
        Index(name = "idx_next_analytics_date", columnList = "next_analytics_date"),
    ]
)
class AnalyticsSetting(
    @Id
    val id: String = ulid(),

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    val user: User,

    @Column(nullable = false)
    var enabled: Boolean = true,

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    var dateRange: AnalyticsRange,

    @Column(nullable = false)
    var nextAnalyticsDate: LocalDate,

    ) : BaseTimeEntity()