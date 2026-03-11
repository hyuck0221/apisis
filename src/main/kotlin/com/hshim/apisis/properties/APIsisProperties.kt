package com.hshim.apisis.properties

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration

@Configuration
@ConfigurationProperties(prefix = "apisis")
data class APIsisProperties(
    var baseUrl: String = "https://apisis.dev"
)
