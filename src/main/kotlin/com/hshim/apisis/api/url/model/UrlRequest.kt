package com.hshim.apisis.api.url.model

import com.hshim.apisis.common.annotation.FieldDescription

data class UrlRequest(
    @FieldDescription("URL (http://... 혹은 https://...)")
    val url: String
)