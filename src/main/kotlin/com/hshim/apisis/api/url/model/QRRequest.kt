package com.hshim.apisis.api.url.model

import com.hshim.apisis.common.annotation.FieldDescription

data class QRRequest(
    @FieldDescription("URL (http://... 혹은 https://...)")
    val url: String,
    @FieldDescription("QR 사이즈 x (default: 300)")
    val x: Int?,
    @FieldDescription("QR 사이즈 y (default: 300)")
    val y: Int?,
)