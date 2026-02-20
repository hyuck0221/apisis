package com.hshim.apisis.api.areacode.service

import com.hshim.apisis.api.areacode.entity.AreaCode
import com.hshim.apisis.api.areacode.model.AreaCodeCondition
import com.hshim.apisis.api.areacode.repository.AreaCodeRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class AreaCodeQueryService (private val areaCodeRepository: AreaCodeRepository) {

    fun findAllPageBy(condition: AreaCodeCondition, pageable: Pageable): Page<AreaCode> {
        return areaCodeRepository.findAllBySearch(
            code = condition.code,
            address = condition.address,
            city = condition.city,
            town = condition.town,
            village = condition.village,
            pageable = pageable,
        )
    }

}