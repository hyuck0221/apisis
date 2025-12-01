package com.hshim.apisis.web

import com.hshim.apisis.user.repository.UserRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping

@Controller
class ViewController(
    private val userRepository: UserRepository
) {

    @GetMapping("/")
    fun index(): String {
        return "index"
    }

    @GetMapping("/login")
    fun login(): String {
        val userId = getCurrentUserId()
        if (userId != null) return "redirect:/dashboard"
        return "login"
    }

    @GetMapping("/dashboard")
    fun dashboard(model: Model): String {
        val userId = getCurrentUserId() ?: return "redirect:/login"

        val user = userRepository.findByIdOrNull(userId)
            ?: return "redirect:/login"

        model.addAttribute("user", user)
        return "dashboard"
    }

    private fun getCurrentUserId(): String? {
        val authentication = SecurityContextHolder.getContext().authentication
        return if (authentication != null && authentication.isAuthenticated && authentication.principal != "anonymousUser") {
            authentication.principal as? String
        } else {
            null
        }
    }
}
