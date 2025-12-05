package com.hshim.apisis.web.service

import com.hshim.apisis.web.model.AnalyticsResponse
import com.hshim.apisis.web.repository.AnalyticsRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.server.ResponseStatusException

@Service
@Transactional(readOnly = true)
class AnalyticsQueryService(
    private val analyticsRepository: AnalyticsRepository,
) {
    fun findAllBy(userId: String): List<AnalyticsResponse> {
        return analyticsRepository.findAllByUserIdOrderBySearchStartDateDesc(userId)
            .map { AnalyticsResponse(it) }
    }

    fun findHtmlById(id: String): String {
        return analyticsRepository.findByIdOrNull(id)?.html
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "not found analytics")
    }
}