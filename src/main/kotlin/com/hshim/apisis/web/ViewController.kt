package com.hshim.apisis.web

import com.hshim.apisis.user.repository.UserRepository
import com.hshim.apisis.user.service.UserUtil.getCurrentUserIdOrNull
import org.springframework.data.repository.findByIdOrNull
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping

@Controller
class ViewController(private val userRepository: UserRepository) {

    @GetMapping("/")
    fun index(): String {
        return "index"
    }

    @GetMapping("/login")
    fun login(): String {
        val userId = getCurrentUserIdOrNull()
        if (userId != null) return "redirect:/dashboard"
        return "login"
    }

    @GetMapping("/dashboard")
    fun dashboard(model: Model): String {
        val userId = getCurrentUserIdOrNull() ?: return "redirect:/login"

        val user = userRepository.findByIdOrNull(userId)
            ?: return "redirect:/login"

        model.addAttribute("user", user)
        return "dashboard"
    }

    @GetMapping("/api-keys")
    fun apiKeys(model: Model): String {
        val userId = getCurrentUserIdOrNull() ?: return "redirect:/login"

        val user = userRepository.findByIdOrNull(userId)
            ?: return "redirect:/login"

        model.addAttribute("user", user)
        return "api-keys"
    }

    @GetMapping("/docs")
    fun docs(model: Model): String {
        val userId = getCurrentUserIdOrNull() ?: return "redirect:/login"

        val user = userRepository.findByIdOrNull(userId)
            ?: return "redirect:/login"

        model.addAttribute("user", user)
        return "api-docs"
    }

    @GetMapping("/apis")
    fun apis(model: Model): String {
        val userId = getCurrentUserIdOrNull() ?: return "redirect:/login"

        val user = userRepository.findByIdOrNull(userId)
            ?: return "redirect:/login"

        model.addAttribute("user", user)
        return "api-list"
    }

    @GetMapping("/usage")
    fun usage(model: Model): String {
        val userId = getCurrentUserIdOrNull() ?: return "redirect:/login"

        val user = userRepository.findByIdOrNull(userId)
            ?: return "redirect:/login"

        model.addAttribute("user", user)
        return "usage"
    }

    @GetMapping("/analytics")
    fun analytics(model: Model): String {
        val userId = getCurrentUserIdOrNull() ?: return "redirect:/login"

        val user = userRepository.findByIdOrNull(userId)
            ?: return "redirect:/login"

        model.addAttribute("user", user)
        return "analytics"
    }

    @GetMapping("/settings")
    fun settings(model: Model): String {
        val userId = getCurrentUserIdOrNull() ?: return "redirect:/login"

        val user = userRepository.findByIdOrNull(userId)
            ?: return "redirect:/login"

        model.addAttribute("user", user)
        return "settings"
    }
}
