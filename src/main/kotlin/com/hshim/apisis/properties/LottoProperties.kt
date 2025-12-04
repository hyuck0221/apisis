package com.hshim.apisis.properties

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration

@Configuration
@ConfigurationProperties(prefix = "lotto")
data class LottoProperties(
    var apiUrl: String = "",
)