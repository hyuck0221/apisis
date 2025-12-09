package com.hshim.apisis.user.entity

import com.hshim.apisis.user.enums.OAuth2Provider
import jakarta.persistence.*
import util.CommonUtil.ulid
import java.time.LocalDateTime

@Entity
@Table(
    name = "oauth2_tokens",
    indexes = [
        Index(name = "idx_user_provider", columnList = "userId,provider")
    ]
)
class OAuth2Token(
    @Id
    val id: String = ulid(),

    @Column(nullable = false)
    val userId: String,

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    val provider: OAuth2Provider,

    @Column(nullable = false, length = 1000)
    val accessToken: String,

    @Column(length = 1000)
    val refreshToken: String?,

    @Column(nullable = false)
    val expiresAt: LocalDateTime,

    @Column(nullable = false)
    val createdDate: LocalDateTime = LocalDateTime.now(),

    @Column(nullable = false)
    val updatedDate: LocalDateTime = LocalDateTime.now(),
)
