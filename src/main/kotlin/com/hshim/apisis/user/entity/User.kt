package com.hshim.apisis.user.entity

import jakarta.persistence.*
import org.springframework.security.oauth2.core.user.OAuth2User
import util.CommonUtil.ulid
import java.time.LocalDateTime

@Entity
@Table(name = "users")
class User(
    @Id
    val id: String = ulid(),

    @Column(nullable = false)
    val email: String,

    @Column(nullable = false)
    val name: String,

    @Column(nullable = false)
    val createdDate: LocalDateTime = LocalDateTime.now(),
) {
    companion object {
        fun of(userId: String) = User(
            id = userId,
            email = "",
            name = "",
        )

        fun ofKakao(oAuth2User: OAuth2User): User {
            val kakaoAccount = oAuth2User.getAttribute<Map<String, Any>>("kakao_account")!!
            val profile = kakaoAccount["profile"] as Map<String, Any>
            return User(
                email = kakaoAccount["email"] as String,
                name = profile["nickname"] as String,
            )
        }

        fun ofGithub(oAuth2User: OAuth2User, email: String): User {
            return User(
                email = email,
                name = oAuth2User.getAttribute<String>("login") ?: "",
            )
        }

        fun ofGoogle(oAuth2User: OAuth2User): User {
            return User(
                email = oAuth2User.getAttribute<String>("email") ?: "",
                name = oAuth2User.getAttribute<String>("name") ?: "",
            )
        }
    }
}