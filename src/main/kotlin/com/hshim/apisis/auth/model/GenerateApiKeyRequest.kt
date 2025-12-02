package com.hshim.apisis.auth.model

import com.hshim.apisis.auth.entity.ApiKey
import com.hshim.apisis.user.entity.User

data class GenerateApiKeyRequest(
    val name: String
) {
    fun toEntity(userId: String, keyValue: String) = ApiKey (
        name = name,
        user = User.of(userId),
        keyValue = keyValue,
    )
}