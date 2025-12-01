package com.hshim.apisis.auth.repository

import com.hshim.apisis.auth.entity.ApiKey
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ApiKeyRepository : JpaRepository<ApiKey, String> {
    fun findByKeyValue(keyValue: String): ApiKey?
    fun findByKeyValueAndIsActive(keyValue: String, isActive: Boolean): ApiKey?
    fun deleteByKeyValue(keyValue: String)
}
