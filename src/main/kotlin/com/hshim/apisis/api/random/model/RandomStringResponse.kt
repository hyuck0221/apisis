package com.hshim.apisis.api.random.model

import com.hshim.apisis.common.annotation.FieldDescription

data class RandomStringResponse (
    @FieldDescription("생성문자")
    val strings: List<String>
)