package com.hshim.apisis.user.entity

import com.hshim.apisis.user.enums.OAuth2Provider
import jakarta.persistence.*
import util.CommonUtil.ulid
import java.time.LocalDateTime

@Entity
@Table(
    name = "user_oauth2_providers",
    indexes = [
        Index(name = "idx_user_id", columnList = "userId"),
        Index(name = "idx_provider_id", columnList = "provider,providerId", unique = true)
    ]
)
class UserOAuth2Provider(
    @Id
    val id: String = ulid(),

    @Column(nullable = false)
    val userId: String,

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    val provider: OAuth2Provider,

    @Column(nullable = false)
    val providerId: String,

    @Column(nullable = false)
    val createdDate: LocalDateTime = LocalDateTime.now(),
)
