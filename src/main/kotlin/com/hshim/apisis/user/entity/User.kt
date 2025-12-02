package com.hshim.apisis.user.entity

import com.hshim.apisis.user.enums.OAuth2Provider
import jakarta.persistence.*
import org.springframework.security.oauth2.core.user.OAuth2User
import util.CommonUtil.ulid
import java.time.LocalDateTime

@Entity
@Table(name = "users")
class User(
    @Id
    val id: String = ulid(),

    @Column(unique = true, nullable = false)
    val email: String,

    @Column(nullable = false)
    val name: String,

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    val provider: OAuth2Provider,

    @Column(nullable = false)
    val providerId: String,

    @Column(nullable = false)
    val createdDate: LocalDateTime = LocalDateTime.now(),
) {
    companion object {
        fun of(userId: String) = User(
            id = userId,
            email = "",
            name = "",
            provider = OAuth2Provider.KAKAO,
            providerId = "",
        )

        fun ofKakao(oAuth2User: OAuth2User): User {
            val kakaoAccount = oAuth2User.getAttribute<Map<String, Any>>("kakao_account")!!
            val profile = kakaoAccount["profile"] as Map<String, Any>
            return User(
                provider = OAuth2Provider.KAKAO,
                providerId = oAuth2User.getAttribute<Long>("id")!!.toString(),
                email = kakaoAccount["email"] as String,
                name = profile["nickname"] as String,
            )
        }
    }
}