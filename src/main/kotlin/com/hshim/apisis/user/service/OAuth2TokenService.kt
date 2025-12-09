package com.hshim.apisis.user.service

import com.hshim.apisis.user.entity.OAuth2Token
import com.hshim.apisis.user.enums.OAuth2Provider
import com.hshim.apisis.user.repository.OAuth2TokenRepository
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.stereotype.Service
import org.springframework.util.LinkedMultiValueMap
import org.springframework.web.client.RestTemplate
import java.time.LocalDateTime
import java.time.ZoneId

@Service
class OAuth2TokenService(
    private val oAuth2TokenRepository: OAuth2TokenRepository,
    @Value("\${spring.security.oauth2.client.registration.kakao.client-id}")
    private val kakaoClientId: String,
    @Value("\${spring.security.oauth2.client.registration.github.client-id}")
    private val githubClientId: String,
    @Value("\${spring.security.oauth2.client.registration.github.client-secret}")
    private val githubClientSecret: String,
    @Value("\${spring.security.oauth2.client.registration.google.client-id}")
    private val googleClientId: String,
    @Value("\${spring.security.oauth2.client.registration.google.client-secret}")
    private val googleClientSecret: String,
) {
    private val restTemplate = RestTemplate()

    fun getOAuth2Token(userId: String, provider: OAuth2Provider): OAuth2Token? {
        return oAuth2TokenRepository.findByUserIdAndProvider(userId, provider)
    }

    fun getValidAccessToken(userId: String, provider: OAuth2Provider): String? {
        val token = oAuth2TokenRepository.findByUserIdAndProvider(userId, provider) ?: return null

        // 토큰이 만료되었거나 5분 이내에 만료될 예정이면 갱신
        if (token.expiresAt.isBefore(LocalDateTime.now().plusMinutes(5))) {
            return refreshAccessToken(token)
        }

        return token.accessToken
    }

    fun refreshAccessToken(token: OAuth2Token): String? {
        if (token.refreshToken == null) return null

        return when (token.provider) {
            OAuth2Provider.KAKAO -> refreshKakaoToken(token)
            OAuth2Provider.GITHUB -> refreshGithubToken(token)
            OAuth2Provider.GOOGLE -> refreshGoogleToken(token)
        }
    }

    private fun refreshKakaoToken(token: OAuth2Token): String? {
        try {
            val headers = HttpHeaders().apply {
                contentType = MediaType.APPLICATION_FORM_URLENCODED
            }

            val body = LinkedMultiValueMap<String, String>().apply {
                add("grant_type", "refresh_token")
                add("client_id", kakaoClientId)
                add("refresh_token", token.refreshToken)
            }

            val request = HttpEntity(body, headers)
            val response = restTemplate.postForObject(
                "https://kauth.kakao.com/oauth/token",
                request,
                Map::class.java
            ) as? Map<*, *> ?: return null

            val newAccessToken = response["access_token"] as? String ?: return null
            val expiresIn = response["expires_in"] as? Int ?: 3600
            val newRefreshToken = response["refresh_token"] as? String ?: token.refreshToken

            val expiresAt = LocalDateTime.now().plusSeconds(expiresIn.toLong())

            val updatedToken = OAuth2Token(
                id = token.id,
                userId = token.userId,
                provider = token.provider,
                accessToken = newAccessToken,
                refreshToken = newRefreshToken,
                expiresAt = expiresAt,
                createdDate = token.createdDate,
                updatedDate = LocalDateTime.now()
            )

            oAuth2TokenRepository.save(updatedToken)
            return newAccessToken
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }

    private fun refreshGithubToken(token: OAuth2Token): String? {
        try {
            val headers = HttpHeaders().apply {
                contentType = MediaType.APPLICATION_FORM_URLENCODED
                set("Accept", "application/json")
            }

            val body = LinkedMultiValueMap<String, String>().apply {
                add("grant_type", "refresh_token")
                add("client_id", githubClientId)
                add("client_secret", githubClientSecret)
                add("refresh_token", token.refreshToken)
            }

            val request = HttpEntity(body, headers)
            val response = restTemplate.postForObject(
                "https://github.com/login/oauth/access_token",
                request,
                Map::class.java
            ) as? Map<*, *> ?: return null

            val newAccessToken = response["access_token"] as? String ?: return null
            val expiresIn = response["expires_in"] as? Int ?: 3600
            val newRefreshToken = response["refresh_token"] as? String ?: token.refreshToken

            val expiresAt = LocalDateTime.now().plusSeconds(expiresIn.toLong())

            val updatedToken = OAuth2Token(
                id = token.id,
                userId = token.userId,
                provider = token.provider,
                accessToken = newAccessToken,
                refreshToken = newRefreshToken,
                expiresAt = expiresAt,
                createdDate = token.createdDate,
                updatedDate = LocalDateTime.now()
            )

            oAuth2TokenRepository.save(updatedToken)
            return newAccessToken
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }

    private fun refreshGoogleToken(token: OAuth2Token): String? {
        try {
            val headers = HttpHeaders().apply {
                contentType = MediaType.APPLICATION_FORM_URLENCODED
            }

            val body = LinkedMultiValueMap<String, String>().apply {
                add("grant_type", "refresh_token")
                add("client_id", googleClientId)
                add("client_secret", googleClientSecret)
                add("refresh_token", token.refreshToken)
            }

            val request = HttpEntity(body, headers)
            val response = restTemplate.postForObject(
                "https://oauth2.googleapis.com/token",
                request,
                Map::class.java
            ) as? Map<*, *> ?: return null

            val newAccessToken = response["access_token"] as? String ?: return null
            val expiresIn = response["expires_in"] as? Int ?: 3600

            val expiresAt = LocalDateTime.now().plusSeconds(expiresIn.toLong())

            val updatedToken = OAuth2Token(
                id = token.id,
                userId = token.userId,
                provider = token.provider,
                accessToken = newAccessToken,
                refreshToken = token.refreshToken,
                expiresAt = expiresAt,
                createdDate = token.createdDate,
                updatedDate = LocalDateTime.now()
            )

            oAuth2TokenRepository.save(updatedToken)
            return newAccessToken
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }

    fun handleTokenExpiration(userId: String, provider: OAuth2Provider): String? {
        val token = oAuth2TokenRepository.findByUserIdAndProvider(userId, provider) ?: return null
        return refreshAccessToken(token)
    }
}
