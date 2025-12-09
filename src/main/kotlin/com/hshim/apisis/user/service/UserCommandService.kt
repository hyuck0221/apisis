package com.hshim.apisis.user.service

import com.hshim.apisis.user.repository.OAuth2TokenRepository
import com.hshim.apisis.user.repository.UserOAuth2ProviderRepository
import com.hshim.apisis.web.repository.ApiKeyRepository
import com.hshim.apisis.user.repository.UserRepository
import com.hshim.apisis.web.repository.AnalyticsSettingRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class UserCommandService(
    private val userRepository: UserRepository,
    private val apiKeyRepository: ApiKeyRepository,
    private val analyticsSettingRepository: AnalyticsSettingRepository,
    private val oauth2TokenRepository: OAuth2TokenRepository,
    private val userOAuth2ProviderRepository: UserOAuth2ProviderRepository,
) {
    fun delete(id: String) {
        apiKeyRepository.deleteAllByUserId(id)
        analyticsSettingRepository.deleteByUserId(id)
        oauth2TokenRepository.deleteAllByUserId(id)
        userOAuth2ProviderRepository.deleteAllByUserId(id)
        userRepository.deleteById(id)
    }
}