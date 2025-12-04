package com.hshim.apisis.web.repository

import com.hshim.apisis.web.entity.ApiKey
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ApiKeyRepository : JpaRepository<ApiKey, String> {
    fun findByKeyValue(keyValue: String): ApiKey?
    fun findByKeyValueAndIsActive(keyValue: String, isActive: Boolean): ApiKey?
    fun deleteByKeyValue(keyValue: String)
    fun deleteAllByUserId(userId: String)
    fun findAllByUserId(userId: String): List<ApiKey>
}
