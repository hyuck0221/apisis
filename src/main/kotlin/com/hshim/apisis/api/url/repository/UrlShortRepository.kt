package com.hshim.apisis.api.url.repository

import com.hshim.apisis.api.url.entity.UrlShort
import org.springframework.data.jpa.repository.JpaRepository

interface UrlShortRepository : JpaRepository<UrlShort, String> {
    fun findTopByOrderByCreateDateDesc(): UrlShort?
    fun findTopByBaseUrl(baseUrl: String): UrlShort?
}