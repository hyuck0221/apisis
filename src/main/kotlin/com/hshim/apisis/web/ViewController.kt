package com.hshim.apisis.web

import com.hshim.apisis.config.JwtUtil
import com.hshim.apisis.user.repository.UserOAuth2ProviderRepository
import com.hshim.apisis.user.repository.UserRepository
import com.hshim.apisis.user.service.UserUtil.getCurrentUserIdOrNull
import com.hshim.apisis.web.service.AnalyticsSettingQueryService
import jakarta.servlet.http.Cookie
import jakarta.servlet.http.HttpServletRequest
import org.springframework.data.repository.findByIdOrNull
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping

@Controller
class ViewController(
    private val userRepository: UserRepository,
    private val analyticsSettingQueryService: AnalyticsSettingQueryService,
    private val jwtUtil: JwtUtil,
    private val userOAuth2ProviderRepository: UserOAuth2ProviderRepository,
) {

    @GetMapping("/")
    fun index(): String {
        return "index"
    }

    @GetMapping("/login")
    fun login(): String {
        val userId = getCurrentUserIdOrNull()
        if (userId != null) {
            // 사용자 존재 여부 확인 후 리다이렉트
            val user = userRepository.findByIdOrNull(userId)
            if (user != null) {
                return "redirect:/dashboard"
            }
            // 사용자가 없으면 인증 정보 제거
            SecurityContextHolder.clearContext()
        }
        return "login"
    }

    @GetMapping("/privacy")
    fun privacy(): String {
        return "privacy"
    }

    @GetMapping("/terms")
    fun terms(): String {
        return "terms"
    }

    @GetMapping("/dashboard")
    fun dashboard(model: Model, request: HttpServletRequest): String {
        val userId = getCurrentUserIdOrNull() ?: return "redirect:/login"

        val user = userRepository.findByIdOrNull(userId)
        if (user == null) {
            // 사용자가 없으면 인증 정보 제거 후 리다이렉트
            SecurityContextHolder.clearContext()
            return "redirect:/login"
        }

        // JWT 토큰에서 로그인 제공자 정보 추출
        val jwtToken = request.cookies?.firstOrNull { it.name == "JWT_TOKEN" }?.value
        val loginProvider = jwtToken?.let { jwtUtil.getLoginProviderFromToken(it) }

        model.addAttribute("user", user)
        model.addAttribute("loginProvider", loginProvider)
        return "dashboard"
    }

    @GetMapping("/api-keys")
    fun apiKeys(model: Model): String {
        val userId = getCurrentUserIdOrNull() ?: return "redirect:/login"

        val user = userRepository.findByIdOrNull(userId)
        if (user == null) {
            SecurityContextHolder.clearContext()
            return "redirect:/login"
        }

        model.addAttribute("user", user)
        return "api-keys"
    }

    @GetMapping("/playground")
    fun playground(model: Model): String {
        val userId = getCurrentUserIdOrNull() ?: return "redirect:/login"

        val user = userRepository.findByIdOrNull(userId)
        if (user == null) {
            SecurityContextHolder.clearContext()
            return "redirect:/login"
        }

        model.addAttribute("user", user)
        return "playground"
    }

    @GetMapping("/docs")
    fun docs(model: Model): String {
        val userId = getCurrentUserIdOrNull() ?: return "redirect:/login"

        val user = userRepository.findByIdOrNull(userId)
        if (user == null) {
            SecurityContextHolder.clearContext()
            return "redirect:/login"
        }

        model.addAttribute("user", user)
        model.addAttribute("userPaymentType", user.license?.paymentType?.name ?: "FREE")
        return "api-docs"
    }

    @GetMapping("/apis")
    fun apis(model: Model): String {
        val userId = getCurrentUserIdOrNull() ?: return "redirect:/login"

        val user = userRepository.findByIdOrNull(userId)
        if (user == null) {
            SecurityContextHolder.clearContext()
            return "redirect:/login"
        }

        model.addAttribute("user", user)
        model.addAttribute("userPaymentType", user.license?.paymentType?.name ?: "FREE")
        return "api-list"
    }

    @GetMapping("/usage")
    fun usage(model: Model): String {
        val userId = getCurrentUserIdOrNull() ?: return "redirect:/login"

        val user = userRepository.findByIdOrNull(userId)
        if (user == null) {
            SecurityContextHolder.clearContext()
            return "redirect:/login"
        }

        model.addAttribute("user", user)
        return "usage"
    }

    @GetMapping("/analytics")
    fun analytics(model: Model): String {
        val userId = getCurrentUserIdOrNull() ?: return "redirect:/login"

        val user = userRepository.findByIdOrNull(userId)
        if (user == null) {
            SecurityContextHolder.clearContext()
            return "redirect:/login"
        }

        model.addAttribute("user", user)
        model.addAttribute("analyticsSetting", analyticsSettingQueryService.findBy(userId))
        return "analytics"
    }

    @GetMapping("/settings")
    fun settings(model: Model, request: HttpServletRequest): String {
        val userId = getCurrentUserIdOrNull() ?: return "redirect:/login"

        val user = userRepository.findByIdOrNull(userId)
        if (user == null) {
            SecurityContextHolder.clearContext()
            return "redirect:/login"
        }

        // JWT 토큰에서 로그인 제공자 정보 추출
        val jwtToken = request.cookies?.firstOrNull { it.name == "JWT_TOKEN" }?.value
        val loginProvider = jwtToken?.let { jwtUtil.getLoginProviderFromToken(it) }

        // 사용자의 모든 OAuth2 제공자 조회
        val userProviders = userOAuth2ProviderRepository.findAllByUserId(userId)
        val providers = userProviders.map { it.provider.name }

        model.addAttribute("user", user)
        model.addAttribute("loginProvider", loginProvider)
        model.addAttribute("userProviders", providers)
        model.addAttribute("analyticsSetting", analyticsSettingQueryService.findBy(userId))
        return "settings"
    }
}
