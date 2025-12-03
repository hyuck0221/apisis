package com.hshim.apisis.properties

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration

@Configuration
@ConfigurationProperties(prefix = "escape.theme")
data class EscapeCrawlingProperties(
    var crawlingUrl: String = "",
    var crawlingAuthorization: String = "",
    var photoBaseUrl: String = "",
)