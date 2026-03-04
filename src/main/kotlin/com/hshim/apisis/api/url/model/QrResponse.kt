package com.hshim.apisis.api.url.model

import com.hshim.apisis.common.annotation.FieldDescription

data class QrResponse (
    @FieldDescription("이미지 (Base64 인토딩)")
    val imageBase64: String,
)