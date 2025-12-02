package com.hshim.apisis.auth.service

import com.hshim.apisis.auth.entity.ApiKey
import com.hshim.apisis.auth.model.ApiKeyResponse
import com.hshim.apisis.auth.repository.ApiKeyRepository
import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class ApiKeyQueryService(private val apiKeyRepository: ApiKeyRepository) {

    @Cacheable(value = ["apiKeys"], key = "#keyValue")
    fun validateApiKey(keyValue: String): ApiKey? {
        return apiKeyRepository.findByKeyValueAndIsActive(keyValue, true)
            ?.takeIf { it.isActive }
    }

    fun findAllByUserId(userId: String): List<ApiKeyResponse> {
        return apiKeyRepository.findAllByUserId(userId).map { ApiKeyResponse(it) }
    }
}