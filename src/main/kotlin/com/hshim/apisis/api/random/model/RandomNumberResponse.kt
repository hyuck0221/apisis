package com.hshim.apisis.api.random.model

import com.hshim.apisis.common.annotation.FieldDescription

data class RandomNumberResponse (
    @FieldDescription("생성숫자")
    val numbers: List<Int>
)