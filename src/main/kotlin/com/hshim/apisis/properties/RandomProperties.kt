package com.hshim.apisis.properties

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration

@Configuration
@ConfigurationProperties(prefix = "random-org")
data class RandomProperties(
    var url: String = ""
)