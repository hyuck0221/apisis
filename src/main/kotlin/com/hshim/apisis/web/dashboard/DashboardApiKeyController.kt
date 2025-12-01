package com.hshim.apisis.web.dashboard

import com.hshim.apisis.auth.model.ApiKeyResponse
import com.hshim.apisis.auth.model.GenerateApiKeyRequest
import com.hshim.apisis.auth.repository.ApiKeyRepository
import com.hshim.apisis.auth.service.ApiKeyCommandService
import com.hshim.apisis.user.repository.UserRepository
import org.springframework.http.HttpStatus
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException

@RestController
@RequestMapping("/dashboard/keys")
class DashboardApiKeyController(
    private val apiKeyCommandService: ApiKeyCommandService,
    private val apiKeyRepository: ApiKeyRepository,
    private val userRepository: UserRepository
) {

    @PostMapping
    fun createApiKey(@RequestBody request: GenerateApiKeyRequest): ApiKeyResponse {
        val userId = getCurrentUserId()
        val user = userRepository.findById(userId).orElseThrow {
            ResponseStatusException(HttpStatus.NOT_FOUND, "사용자를 찾을 수 없습니다")
        }

        val apiKey = apiKeyCommandService.generate(request.name, user)
        return ApiKeyResponse(apiKey)
    }

    @GetMapping
    fun getApiKeys(): List<ApiKeyResponse> {
        val userId = getCurrentUserId()
        val user = userRepository.findById(userId).orElseThrow {
            ResponseStatusException(HttpStatus.NOT_FOUND, "사용자를 찾을 수 없습니다")
        }

        return apiKeyRepository.findAll()
            .filter { it.user.id == user.id }
            .map { ApiKeyResponse(it) }
    }

    private fun getCurrentUserId(): String {
        return SecurityContextHolder.getContext().authentication?.principal as? String
            ?: throw ResponseStatusException(HttpStatus.UNAUTHORIZED, "로그인이 필요합니다")
    }

    @PutMapping("/{keyValue}/activate")
    fun activateApiKey(@PathVariable keyValue: String) {
        apiKeyCommandService.activate(keyValue)
    }

    @PutMapping("/{keyValue}/deactivate")
    fun deactivateApiKey(@PathVariable keyValue: String) {
        apiKeyCommandService.deactivate(keyValue)
    }

    @DeleteMapping("/{keyValue}")
    fun deleteApiKey(@PathVariable keyValue: String) {
        apiKeyCommandService.delete(keyValue)
    }
}
