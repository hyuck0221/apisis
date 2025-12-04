package com.hshim.apisis.web.service

import com.hshim.apisis.web.entity.ApiKey
import com.hshim.apisis.web.model.ApiKeyResponse
import com.hshim.apisis.web.repository.ApiKeyRepository
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