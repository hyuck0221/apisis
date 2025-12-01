package com.hshim.apisis.user.repository

import com.hshim.apisis.user.entity.User
import com.hshim.apisis.user.enums.OAuth2Provider
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface UserRepository : JpaRepository<User, String> {
    fun findByProviderAndProviderId(provider: OAuth2Provider, providerId: String): User?
}
