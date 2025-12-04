package com.hshim.apisis.web.model

import com.hshim.apisis.web.entity.ApiKey
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