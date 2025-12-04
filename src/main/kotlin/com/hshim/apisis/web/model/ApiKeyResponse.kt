package com.hshim.apisis.web.model

import com.hshim.apisis.web.entity.ApiKey
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