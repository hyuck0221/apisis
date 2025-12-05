package com.hshim.apisis.web.entity

import com.hshim.apisis.api.base.entity.BaseTimeEntity
import com.hshim.apisis.user.entity.User
import jakarta.persistence.*
import util.CommonUtil.ulid

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

) : BaseTimeEntity()
