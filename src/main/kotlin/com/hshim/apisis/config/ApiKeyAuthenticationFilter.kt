package com.hshim.apisis.config

import com.hshim.apisis.web.service.ApiKeyQueryService
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter

@Component
class ApiKeyAuthenticationFilter(
    private val apiKeyQueryService: ApiKeyQueryService
) : OncePerRequestFilter() {

    companion object {
        private const val API_KEY_HEADER = "X-API-Key"
    }

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        if (request.method == "OPTIONS") {
            filterChain.doFilter(request, response)
            return
        }

        if (request.requestURI.startsWith("/api/")) {
            val apiKey = request.getHeader(API_KEY_HEADER)
            if (apiKey != null) {
                val validatedKey = apiKeyQueryService.validateApiKey(apiKey)
                if (validatedKey != null) {
                    val authorities = listOf(SimpleGrantedAuthority("ROLE_API_USER"))
                    val authentication = UsernamePasswordAuthenticationToken(validatedKey.keyValue, null, authorities)
                    SecurityContextHolder.getContext().authentication = authentication
                }
            }
        }

        filterChain.doFilter(request, response)
    }
}
