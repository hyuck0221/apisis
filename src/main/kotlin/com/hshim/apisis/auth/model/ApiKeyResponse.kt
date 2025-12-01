package com.hshim.apisis.auth.model

import com.hshim.apisis.auth.entity.ApiKey
import java.time.LocalDateTime

data class ApiKeyResponse(
    val id: String,
    val keyValue: String,
    val name: String,
    val active: Boolean,
    val createdDate: LocalDateTime
) {
    constructor(apiKey: ApiKey) : this(
        id = apiKey.id,
        keyValue = apiKey.keyValue,
        name = apiKey.name,
        active = apiKey.isActive,
        createdDate = apiKey.createDate
    )
}