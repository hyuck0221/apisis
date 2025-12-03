package com.hshim.apisis.config

import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter

@Component
class JwtAuthenticationFilter(
    private val jwtUtil: JwtUtil
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
                        // 유효하지 않은 토큰이면 쿠키 삭제
                        clearJWTCookie(response)
                    }
                } catch (e: Exception) {
                    // 토큰 검증 실패 시 쿠키 삭제
                    clearJWTCookie(response)
                }
            }
        }

        filterChain.doFilter(request, response)
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
