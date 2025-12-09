package com.hshim.apisis.config

import com.hshim.apisis.user.entity.OAuth2Token
import com.hshim.apisis.user.enums.OAuth2Provider
import com.hshim.apisis.user.repository.OAuth2TokenRepository
import com.hshim.apisis.user.repository.UserRepository
import jakarta.servlet.http.Cookie
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.core.Authentication
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService
import org.springframework.security.oauth2.core.user.OAuth2User
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler
import org.springframework.stereotype.Component
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.Date

@Component
class OAuth2SuccessHandler(
    private val jwtUtil: JwtUtil,
    private val oAuth2TokenRepository: OAuth2TokenRepository,
    private val userRepository: UserRepository,
    private val authorizedClientService: OAuth2AuthorizedClientService
) : SimpleUrlAuthenticationSuccessHandler() {

    override fun onAuthenticationSuccess(
        request: HttpServletRequest,
        response: HttpServletResponse,
        authentication: Authentication
    ) {
        val oAuth2User = authentication.principal as OAuth2User
        val userId = oAuth2User.attributes["userId"] as String
        val loginProvider = oAuth2User.attributes["loginProvider"] as String

        // 사용자 정보 조회
        val user = userRepository.findById(userId).orElseThrow()

        // OAuth2 클라이언트에서 액세스 토큰과 리프레시 토큰 가져오기
        val authorizedClient = authorizedClientService.loadAuthorizedClient<OAuth2AuthorizedClient>(
            loginProvider.lowercase(),
            authentication.name
        )

        if (authorizedClient != null) {
            val accessToken = authorizedClient.accessToken
            val refreshToken = authorizedClient.refreshToken

            // 기존 토큰 삭제 후 새로운 토큰 저장
            val provider = OAuth2Provider.valueOf(loginProvider)
            oAuth2TokenRepository.findByUserIdAndProvider(userId, provider)?.let {
                oAuth2TokenRepository.delete(it)
            }

            val expiresAt = accessToken.expiresAt?.let {
                LocalDateTime.ofInstant(it, ZoneId.systemDefault())
            } ?: LocalDateTime.now().plusHours(1)

            val oauth2Token = OAuth2Token(
                userId = userId,
                provider = provider,
                accessToken = accessToken.tokenValue,
                refreshToken = refreshToken?.tokenValue,
                expiresAt = expiresAt
            )

            oAuth2TokenRepository.save(oauth2Token)

            // JWT 토큰 생성 (OAuth 토큰 만료 시간과 동기화)
            val jwtExpiration = Date.from(expiresAt.atZone(ZoneId.systemDefault()).toInstant())
            val token = jwtUtil.generateToken(userId, loginProvider, jwtExpiration)

            // 쿠키에 JWT 저장 (OAuth 토큰 만료 시간과 동일)
            val maxAge = ((expiresAt.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli() -
                           Instant.now().toEpochMilli()) / 1000).toInt()

            val cookie = Cookie("JWT_TOKEN", token).apply {
                this.maxAge = maxAge
                path = "/"
                isHttpOnly = true
                secure = request.isSecure
            }

            // SameSite 속성을 추가하여 쿠키 유지
            response.addHeader("Set-Cookie",
                "${cookie.name}=${cookie.value}; Max-Age=${cookie.maxAge}; Path=${cookie.path}; HttpOnly; SameSite=Lax" +
                if (cookie.secure) "; Secure" else ""
            )
        } else {
            // OAuth2 토큰을 가져오지 못한 경우 기본 만료 시간으로 JWT 생성
            val token = jwtUtil.generateToken(userId, loginProvider)

            val cookie = Cookie("JWT_TOKEN", token).apply {
                maxAge = 24 * 60 * 60
                path = "/"
                isHttpOnly = true
                secure = request.isSecure
            }

            // SameSite 속성을 추가하여 쿠키 유지
            response.addHeader("Set-Cookie",
                "${cookie.name}=${cookie.value}; Max-Age=${cookie.maxAge}; Path=${cookie.path}; HttpOnly; SameSite=Lax" +
                if (cookie.secure) "; Secure" else ""
            )
        }

        redirectStrategy.sendRedirect(request, response, "/dashboard")
    }
}
