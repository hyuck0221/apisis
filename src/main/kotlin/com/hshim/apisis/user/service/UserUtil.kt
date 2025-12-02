package com.hshim.apisis.user.service

import org.springframework.http.HttpStatus
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.server.ResponseStatusException

object UserUtil {
    fun getCurrentUserId(): String {
        return getCurrentUserIdOrNull()
            ?: throw ResponseStatusException(HttpStatus.UNAUTHORIZED, "로그인이 필요합니다")
    }

    fun getCurrentUserIdOrNull(): String? {
        val authentication = SecurityContextHolder.getContext().authentication
        return if (authentication != null && authentication.isAuthenticated && authentication.principal != "anonymousUser") {
            authentication.principal as? String
        } else {
            null
        }
    }
}