package com.hshim.apisis.api.escape.service

import com.hshim.apisis.api.escape.entity.EscapeCafe
import com.hshim.apisis.api.escape.model.EscapeCafeBoundsSearchCondition
import com.hshim.apisis.api.escape.model.EscapeCafeLocationResponse
import com.hshim.apisis.api.escape.model.EscapeCafeSearchCondition
import com.hshim.apisis.api.escape.repository.EscapeCafeRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class EscapeCafeQueryService(private val escapeCafeRepository: EscapeCafeRepository) {

    fun findAllCafesLocation(): List<EscapeCafeLocationResponse> {
        return escapeCafeRepository.findAllByGroupByLocation()
            .map { EscapeCafeLocationResponse(it) }
    }

    fun findAllPageBy(condition: EscapeCafeSearchCondition, pageable: Pageable): Page<EscapeCafe> {
        return escapeCafeRepository.findAllBySearch(
            condition.search ?: "",
            condition.onlyOpen ?: true,
            condition.location ?: "",
            condition.area ?: "",
            pageable,
        )
    }

    fun findAllByBounds(condition: EscapeCafeBoundsSearchCondition): List<EscapeCafe> {
        return escapeCafeRepository.findAllByBounds(
            minLat = condition.minLat,
            maxLat = condition.maxLat,
            minLng = condition.minLng,
            maxLng = condition.maxLng,
            onlyOpen = condition.onlyOpen ?: false,
        )
    }

    fun findAllByNames(names: List<String>): List<EscapeCafe> {
        return escapeCafeRepository.findAllByNameIn(names)
    }
}
