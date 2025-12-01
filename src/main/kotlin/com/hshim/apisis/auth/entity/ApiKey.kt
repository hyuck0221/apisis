package com.hshim.apisis.auth.entity

import com.hshim.apisis.user.entity.User
import jakarta.persistence.*
import util.CommonUtil.ulid
import java.time.LocalDateTime

@Entity
@Table(name = "api_key")
class ApiKey(
    @Id
    val id: String = ulid(),

    @Column(unique = true, nullable = false, length = 64)
    val keyValue: String,

    @Column(nullable = false)
    val name: String,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    val user: User,

    @Column(nullable = false)
    var isActive: Boolean = true,

    @Column(nullable = false)
    val createDate: LocalDateTime = LocalDateTime.now()
)
