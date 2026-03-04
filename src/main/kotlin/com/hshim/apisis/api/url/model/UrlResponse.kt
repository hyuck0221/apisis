package com.hshim.apisis.api.url.model

import com.hshim.apisis.common.annotation.FieldDescription

data class UrlResponse(
    @FieldDescription("URL")
    val url: String
)