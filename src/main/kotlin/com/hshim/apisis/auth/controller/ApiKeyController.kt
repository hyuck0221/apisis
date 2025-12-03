package com.hshim.apisis.auth.controller

import com.hshim.apisis.auth.model.ApiKeyResponse
import com.hshim.apisis.auth.model.GenerateApiKeyRequest
import com.hshim.apisis.auth.service.ApiKeyCommandService
import com.hshim.apisis.auth.service.ApiKeyQueryService
import com.hshim.apisis.user.service.UserUtil.getCurrentUserId
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/auth/keys")
class ApiKeyController(
    private val apiKeyQueryService: ApiKeyQueryService,
    private val apiKeyCommandService: ApiKeyCommandService,
) {
    @PostMapping
    fun init(@RequestBody request: GenerateApiKeyRequest): ApiKeyResponse {
        val userId = getCurrentUserId()
        return apiKeyCommandService.init(request, userId)
    }

    @GetMapping
    fun findAllBy(): List<ApiKeyResponse> {
        val userId = getCurrentUserId()
        return apiKeyQueryService.findAllByUserId(userId)
    }

    @PutMapping("/{keyValue}/name")
    fun updateName(
        @PathVariable keyValue: String,
        @RequestParam name: String,
    ) = apiKeyCommandService.updateName(keyValue, name)

    @PutMapping("/{keyValue}/activate")
    fun activateApiKey(@PathVariable keyValue: String) = apiKeyCommandService.activate(keyValue)

    @PutMapping("/{keyValue}/deactivate")
    fun deactivateApiKey(@PathVariable keyValue: String) = apiKeyCommandService.deactivate(keyValue)

    @DeleteMapping("/{keyValue}")
    fun deleteApiKey(@PathVariable keyValue: String) = apiKeyCommandService.delete(keyValue)
}