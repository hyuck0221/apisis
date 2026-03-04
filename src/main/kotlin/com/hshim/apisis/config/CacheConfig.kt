package com.hshim.apisis.config

import org.springframework.cache.annotation.EnableCaching
import org.springframework.context.annotation.Configuration

@Configuration
@EnableCaching
class CacheConfig {
    companion object {
        const val ESCAPE_CAFE_SEARCH = "escape-cafe-search"
        const val ESCAPE_THEME_SEARCH = "escape-theme-search"
        const val ESCAPE_THEME_SEARCH_BY_CAFES = "escape-theme-search-by-cafes"
    }
}
