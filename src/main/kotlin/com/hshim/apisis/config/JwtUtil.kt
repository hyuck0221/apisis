package com.hshim.apisis.config

import io.jsonwebtoken.ExpiredJwtException
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.stereotype.Component
import java.util.*
import javax.crypto.SecretKey

@Component
@EnableConfigurationProperties(JwtProperties::class)
class JwtUtil(
    private val jwtProperties: JwtProperties
) {
    private val secretKey: SecretKey = Keys.hmacShaKeyFor(jwtProperties.secret.toByteArray())

    fun generateToken(userId: String, loginProvider: String? = null, expirationDate: Date? = null): String {
        val now = Date()
        val expiryDate = expirationDate ?: Date(now.time + jwtProperties.expiration)

        val builder = Jwts.builder()
            .subject(userId)
            .issuedAt(now)
            .expiration(expiryDate)

        if (loginProvider != null) {
            builder.claim("loginProvider", loginProvider)
        }

        return builder.signWith(secretKey).compact()
    }

    fun validateToken(token: String): Boolean {
        return try {
            Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
            true
        } catch (e: Exception) {
            false
        }
    }

    fun getUserIdFromToken(token: String): String? {
        return try {
            Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .payload
                .subject
        } catch (e: ExpiredJwtException) {
            // 만료된 토큰에서도 클레임 추출
            e.claims.subject
        } catch (e: Exception) {
            null
        }
    }

    fun getLoginProviderFromToken(token: String): String? {
        return try {
            Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .payload
                .get("loginProvider", String::class.java)
        } catch (e: ExpiredJwtException) {
            // 만료된 토큰에서도 클레임 추출
            e.claims.get("loginProvider", String::class.java)
        } catch (e: Exception) {
            null
        }
    }
}
