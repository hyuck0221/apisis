package com.hshim.apisis.api.escape.service

import com.hshim.apisis.api.escape.model.EscapeCafeRequest
import com.hshim.apisis.api.escape.model.EscapeCafeResponse
import com.hshim.apisis.api.escape.repository.EscapeCafeRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class EscapeCafeCommandService(private val escapeCafeRepository: EscapeCafeRepository) {
    fun init(request: EscapeCafeRequest): EscapeCafeResponse {
        return escapeCafeRepository.findByNameAndLocationAndArea(request.name, request.location, request.area)
            ?.let { EscapeCafeResponse(it) }
            ?: EscapeCafeResponse(escapeCafeRepository.save(request.toEntity()))
    }
}