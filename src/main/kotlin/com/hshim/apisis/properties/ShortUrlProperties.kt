package com.hshim.apisis.properties

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration

@Configuration
@ConfigurationProperties(prefix = "short-url")
data class ShortUrlProperties(
    var url: String = ""
)