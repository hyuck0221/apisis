package com.hshim.apisis.config

import com.hshim.apisis.user.enums.OAuth2Provider
import com.hshim.apisis.user.service.OAuth2TokenService
import jakarta.servlet.FilterChain
import jakarta.servlet.http.Cookie
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter
import java.time.ZoneId
import java.util.*

@Component
class JwtAuthenticationFilter(
    private val jwtUtil: JwtUtil,
    private val oAuth2TokenService: OAuth2TokenService
) : OncePerRequestFilter() {

    companion object {
        private const val AUTHORIZATION_HEADER = "Authorization"
        private const val BEARER_PREFIX = "Bearer "
        private const val JWT_COOKIE_NAME = "JWT_TOKEN"
    }

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val requestPath = request.requestURI
        if (!requestPath.startsWith("/api/")) {
            val token = extractToken(request)

            if (token != null) {
                try {
                    if (jwtUtil.validateToken(token)) {
                        val userId = jwtUtil.getUserIdFromToken(token)
                        if (userId != null) {
                            val authorities = listOf(SimpleGrantedAuthority("ROLE_USER"))
                            val authentication = UsernamePasswordAuthenticationToken(userId, null, authorities)
                            SecurityContextHolder.getContext().authentication = authentication
                        }
                    } else {
                        // JWT가 만료된 경우 OAuth 리프레시 토큰으로 재발급 시도
                        val userId = jwtUtil.getUserIdFromToken(token)
                        val loginProvider = jwtUtil.getLoginProviderFromToken(token)

                        if (userId != null && loginProvider != null) {
                            val refreshed = tryRefreshToken(userId, loginProvider, request, response)
                            if (refreshed) {
                                // 리프레시 성공 시 인증 설정
                                val authorities = listOf(SimpleGrantedAuthority("ROLE_USER"))
                                val authentication = UsernamePasswordAuthenticationToken(userId, null, authorities)
                                SecurityContextHolder.getContext().authentication = authentication
                            } else {
                                // 리프레시 실패 시 쿠키 삭제
                                clearJWTCookie(response)
                            }
                        } else {
                            clearJWTCookie(response)
                        }
                    }
                } catch (e: Exception) {
                    // 토큰 검증 실패 시 쿠키 삭제
                    clearJWTCookie(response)
                }
            }
        }

        filterChain.doFilter(request, response)
    }

    private fun tryRefreshToken(
        userId: String,
        loginProvider: String,
        request: HttpServletRequest,
        response: HttpServletResponse
    ): Boolean {
        return try {
            val provider = OAuth2Provider.valueOf(loginProvider)
            val newAccessToken = oAuth2TokenService.getValidAccessToken(userId, provider)

            if (newAccessToken != null) {
                // OAuth 토큰에서 새로운 만료 시간 가져오기
                val oauth2Token = oAuth2TokenService.getOAuth2Token(userId, provider)

                if (oauth2Token != null) {
                    val jwtExpiration = Date.from(oauth2Token.expiresAt.atZone(ZoneId.systemDefault()).toInstant())
                    val newJwtToken = jwtUtil.generateToken(userId, loginProvider, jwtExpiration)

                    // 새로운 JWT를 쿠키에 저장
                    val maxAge = ((oauth2Token.expiresAt.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli() -
                                   System.currentTimeMillis()) / 1000).toInt()

                    response.addHeader("Set-Cookie",
                        "$JWT_COOKIE_NAME=$newJwtToken; Max-Age=$maxAge; Path=/; HttpOnly; SameSite=Lax" +
                        if (request.isSecure) "; Secure" else ""
                    )

                    true
                } else {
                    false
                }
            } else {
                false
            }
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    private fun clearJWTCookie(response: HttpServletResponse) {
        response.addHeader("Set-Cookie",
            "$JWT_COOKIE_NAME=; Max-Age=0; Path=/; HttpOnly; SameSite=Lax")
    }

    private fun extractToken(request: HttpServletRequest): String? {
        val bearerToken = request.getHeader(AUTHORIZATION_HEADER)
        if (bearerToken != null && bearerToken.startsWith(BEARER_PREFIX)) {
            return bearerToken.substring(BEARER_PREFIX.length)
        }

        val cookies = request.cookies
        if (cookies != null) {
            for (cookie in cookies) {
                if (cookie.name == JWT_COOKIE_NAME) {
                    return cookie.value
                }
            }
        }

        return null
    }
}
