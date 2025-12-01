package com.hshim.apisis.auth.service

import com.hshim.apisis.auth.entity.ApiKey
import com.hshim.apisis.auth.repository.ApiKeyRepository
import com.hshim.apisis.user.entity.User
import org.springframework.cache.annotation.CacheEvict
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.security.SecureRandom
import java.util.*

@Service
@Transactional
class ApiKeyCommandService(private val apiKeyRepository: ApiKeyRepository) {
    companion object {
        private const val API_KEY_LENGTH = 32
        private val secureRandom = SecureRandom()
    }

    fun generate(name: String, user: User): ApiKey {
        val keyValue = generateSecureKey()
        val apiKey = ApiKey(
            keyValue = keyValue,
            name = name,
            user = user
        )
        return apiKeyRepository.save(apiKey)
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
        apiKeyRepository.deleteByKeyValue(keyValue)
    }

    private fun generateSecureKey(): String {
        val bytes = ByteArray(API_KEY_LENGTH)
        secureRandom.nextBytes(bytes)
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes)
    }
}
