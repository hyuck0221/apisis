package com.hshim.apisis.auth.service

import com.hshim.apisis.auth.model.ApiKeyResponse
import com.hshim.apisis.auth.model.GenerateApiKeyRequest
import com.hshim.apisis.auth.repository.ApiCallLogRepository
import com.hshim.apisis.auth.repository.ApiKeyRepository
import org.springframework.cache.annotation.CacheEvict
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.security.SecureRandom
import java.util.*

@Service
@Transactional
class ApiKeyCommandService(
    private val apiKeyRepository: ApiKeyRepository,
    private val apiCallLogRepository: ApiCallLogRepository
) {
    companion object {
        private const val API_KEY_LENGTH = 32
        private val secureRandom = SecureRandom()
    }

    private fun generateSecureKey(): String {
        val bytes = ByteArray(API_KEY_LENGTH)
        secureRandom.nextBytes(bytes)
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes)
    }

    fun init(request: GenerateApiKeyRequest, userId: String): ApiKeyResponse {
        val keyValue = generateSecureKey()
        val apiKey = apiKeyRepository.save(request.toEntity(userId, keyValue))
        return ApiKeyResponse(apiKey)
    }

    @CacheEvict(value = ["apiKeys"], key = "#keyValue")
    fun activate(keyValue: String) {
        val apiKey = apiKeyRepository.findByKeyValue(keyValue) ?: return
        apiKey.isActive = true
    }

    @CacheEvict(value = ["apiKeys"], key = "#keyValue")
    fun deactivate(keyValue: String) {
        val apiKey = apiKeyRepository.findByKeyValue(keyValue) ?: return
        apiKey.isActive = false
    }

    @CacheEvict(value = ["apiKeys"], key = "#keyValue")
    fun delete(keyValue: String) {
        apiCallLogRepository.deleteAllByApiKeyValue(keyValue)
        apiKeyRepository.deleteByKeyValue(keyValue)
    }
}
