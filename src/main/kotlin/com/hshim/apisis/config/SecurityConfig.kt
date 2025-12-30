package com.hshim.apisis.config

import com.hshim.apisis.user.service.CustomOAuth2UserService
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter

import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer

@Configuration
@EnableWebSecurity
class SecurityConfig(
    private val apiKeyAuthenticationFilter: ApiKeyAuthenticationFilter,
    private val jwtAuthenticationFilter: JwtAuthenticationFilter,
    private val customOAuth2UserService: CustomOAuth2UserService,
    private val oAuth2SuccessHandler: OAuth2SuccessHandler
) {

    @Bean
    fun corsConfigurationSource(): org.springframework.web.cors.CorsConfigurationSource {
        val configuration = CorsConfiguration()
        configuration.allowedOriginPatterns = listOf("*")
        configuration.allowedMethods = listOf("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH")
        configuration.allowedHeaders = listOf("*")
        configuration.allowCredentials = true
        val source = UrlBasedCorsConfigurationSource()
        source.registerCorsConfiguration("/**", configuration)
        return source
    }

    @Bean
    fun webSecurityCustomizer(): WebSecurityCustomizer {
        return WebSecurityCustomizer { web ->
            web.ignoring().requestMatchers("/mcp/**")
        }
    }

    @Bean
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
        http
            .csrf { it.disable() }
            .cors { it.configurationSource(corsConfigurationSource()) }
            .sessionManagement {
                it.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            }
            .authorizeHttpRequests { auth ->
                auth
                    // 정적 리소스 및 로그인 페이지
                    .requestMatchers("/", "/login", "/css/**", "/js/**", "/images/**").permitAll()

                    // MCP SSE 엔드포인트 허용 (/mcp/sse, /mcp/message 등)
                    .requestMatchers("/mcp/**").permitAll()

                    // OAuth2 콜백 경로
                    .requestMatchers("/login/oauth2/**", "/oauth2/**").permitAll()

                    // JWT 기반 인증이 필요한 경로 (대시보드 및 관리)
                    .requestMatchers("/dashboard/**", "/web/**").authenticated()

                    // API 키 헤더 기반 인증이 필요한 경로 (실제 API들)
                    .requestMatchers("/api/**").authenticated()

                    // 나머지는 허용
                    .anyRequest().permitAll()
            }
            .oauth2Login { oauth2 ->
                oauth2
                    .loginPage("/login")
                    .userInfoEndpoint { it.userService(customOAuth2UserService) }
                    .successHandler(oAuth2SuccessHandler)
            }
            .logout { logout ->
                logout
                    .logoutUrl("/logout")
                    .logoutSuccessUrl("/")
                    .invalidateHttpSession(true)
                    .deleteCookies("JSESSIONID", "JWT_TOKEN")
            }
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter::class.java)
            .addFilterBefore(apiKeyAuthenticationFilter, UsernamePasswordAuthenticationFilter::class.java)
            .exceptionHandling {
                it.authenticationEntryPoint { request, response, authException ->
                    if (request.requestURI.startsWith("/api/")) {
                        response.status = 401
                        response.contentType = "application/json"
                        response.writer.write("{\"error\": \"unauthorized\", \"message\": \"${authException.message}\"}")
                    } else {
                        response.sendRedirect("/login")
                    }
                }
            }

        return http.build()
    }
}
