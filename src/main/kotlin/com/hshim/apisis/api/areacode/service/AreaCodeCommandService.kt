package com.hshim.apisis.api.areacode.service

import com.hshim.apisis.api.areacode.model.AreaCodeExcelInfo
import com.hshim.apisis.api.areacode.repository.AreaCodeRepository
import io.clroot.excel.parser.ParseResult
import io.clroot.excel.parser.parseExcel
import jakarta.persistence.EntityManager
import org.springframework.core.io.ClassPathResource
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

private const val BATCH_SIZE = 500

@Service
@Transactional
class AreaCodeCommandService(
    private val areaCodeRepository: AreaCodeRepository,
    private val entityManager: EntityManager,
) {

    fun parsing() {
        when (val result = parseExcel<AreaCodeExcelInfo>(ClassPathResource("excel/areacode_01.xlsx").inputStream)) {
            is ParseResult.Success -> {
                deleteAll()
                result.data.forEachIndexed { index, info ->
                    entityManager.persist(info.toEntity())
                    if ((index + 1) % BATCH_SIZE == 0) {
                        entityManager.flush()
                        entityManager.clear()
                    }
                }
                entityManager.flush()
                entityManager.clear()
            }

            is ParseResult.Failure -> throw IllegalStateException("[AreaCode] Parsing failed")
        }
    }

    fun deleteAll() {
        areaCodeRepository.deleteAll()
    }

}