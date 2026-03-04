package com.hshim.apisis.api.random.model

import com.hshim.apisis.common.annotation.FieldDescription
import org.springframework.http.HttpStatus
import org.springframework.web.server.ResponseStatusException

data class GenerateRandomStringCondition(
    @FieldDescription("생성 수 (default:1, max:100)")
    val cnt: Int?,
) {
    init {
        when {
            cnt != null && cnt > 100 -> throw ResponseStatusException(HttpStatus.BAD_REQUEST, "cnt is too many")
            cnt != null && cnt < 1 -> throw ResponseStatusException(HttpStatus.BAD_REQUEST, "cnt is too less")
        }
    }
}