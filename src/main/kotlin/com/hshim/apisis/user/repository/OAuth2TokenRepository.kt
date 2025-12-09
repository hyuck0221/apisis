package com.hshim.apisis.user.repository

import com.hshim.apisis.user.entity.OAuth2Token
import com.hshim.apisis.user.enums.OAuth2Provider
import org.springframework.data.jpa.repository.JpaRepository

interface OAuth2TokenRepository : JpaRepository<OAuth2Token, String> {
    fun findByUserIdAndProvider(userId: String, provider: OAuth2Provider): OAuth2Token?
}
