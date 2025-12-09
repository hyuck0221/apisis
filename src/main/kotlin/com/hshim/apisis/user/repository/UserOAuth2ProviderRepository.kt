package com.hshim.apisis.user.repository

import com.hshim.apisis.user.entity.UserOAuth2Provider
import com.hshim.apisis.user.enums.OAuth2Provider
import org.springframework.data.jpa.repository.JpaRepository

interface UserOAuth2ProviderRepository : JpaRepository<UserOAuth2Provider, String> {
    fun findByProviderAndProviderId(provider: OAuth2Provider, providerId: String): UserOAuth2Provider?
    fun findAllByUserId(userId: String): List<UserOAuth2Provider>
    fun deleteAllByUserId(userId: String)
}
