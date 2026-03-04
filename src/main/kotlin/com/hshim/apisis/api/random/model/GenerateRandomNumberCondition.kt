package com.hshim.apisis.api.random.model

import com.hshim.apisis.common.annotation.FieldDescription
import org.springframework.http.HttpStatus
import org.springframework.web.server.ResponseStatusException

data class GenerateRandomNumberCondition(
    @FieldDescription("생성 수 (default:1, max:100)")
    val cnt: Int?,
    @FieldDescription("최소값 (-1000000000 ~ 999999999)")
    val min: Int,
    @FieldDescription("최대값 (-999999999 ~ 1000000000)")
    val max: Int,
) {
    init {
        when {
            cnt != null && cnt > 100 -> throw ResponseStatusException(HttpStatus.BAD_REQUEST, "cnt is too many")
            cnt != null && cnt < 1 -> throw ResponseStatusException(HttpStatus.BAD_REQUEST, "cnt is too less")
            min > max -> throw ResponseStatusException(HttpStatus.BAD_REQUEST, "min cannot be greater than the max")
            min == max -> throw ResponseStatusException(HttpStatus.BAD_REQUEST, "min cannot be equal to the max")
            min > 99_9999_999 -> throw ResponseStatusException(HttpStatus.BAD_REQUEST, "min is too bigger")
            min < -1_000_000_000 -> throw ResponseStatusException(HttpStatus.BAD_REQUEST, "min is too smaller")
            max > 1_000_000_000 -> throw ResponseStatusException(HttpStatus.BAD_REQUEST, "max is too bigger")
        }
    }
}