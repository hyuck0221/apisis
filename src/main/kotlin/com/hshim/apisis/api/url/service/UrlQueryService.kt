package com.hshim.apisis.api.url.service

import com.hshim.apisis.api.url.model.UrlViewResponse
import com.hshim.apisis.api.url.repository.UrlShortRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.server.ResponseStatusException

@Service
@Transactional(readOnly = true)
class UrlQueryService(private val urlShortRepository: UrlShortRepository) {
    fun urlViewCntByAPIsis(id: String): UrlViewResponse {
        return urlShortRepository.findByIdOrNull(id)
            ?.let { UrlViewResponse(it) }
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "url not found")
    }
}