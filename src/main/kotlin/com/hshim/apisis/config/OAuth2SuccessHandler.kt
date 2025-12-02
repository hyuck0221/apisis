package com.hshim.apisis.config

import jakarta.servlet.http.Cookie
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.core.Authentication
import org.springframework.security.oauth2.core.user.OAuth2User
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler
import org.springframework.stereotype.Component

@Component
class OAuth2SuccessHandler(
    private val jwtUtil: JwtUtil
) : SimpleUrlAuthenticationSuccessHandler() {

    override fun onAuthenticationSuccess(
        request: HttpServletRequest,
        response: HttpServletResponse,
        authentication: Authentication
    ) {
        val oAuth2User = authentication.principal as OAuth2User
        val userId = oAuth2User.attributes["userId"] as String

        // JWT 토큰 생성
        val token = jwtUtil.generateToken(userId)

        // 쿠키에 JWT 저장 (30일 유지)
        val cookie = Cookie("JWT_TOKEN", token).apply {
            maxAge = 30 * 24 * 60 * 60 // 30일
            path = "/"
            isHttpOnly = true
            secure = request.isSecure // HTTPS 환경에서 자동으로 true
        }

        // SameSite 속성을 추가하여 쿠키 유지
        response.addHeader("Set-Cookie",
            "${cookie.name}=${cookie.value}; Max-Age=${cookie.maxAge}; Path=${cookie.path}; HttpOnly; SameSite=Lax" +
            if (cookie.secure) "; Secure" else ""
        )

        redirectStrategy.sendRedirect(request, response, "/dashboard")
    }
}
