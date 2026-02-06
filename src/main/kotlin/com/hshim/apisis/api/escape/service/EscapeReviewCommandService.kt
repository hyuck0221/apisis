package com.hshim.apisis.api.escape.service

import com.hshim.apisis.api.escape.model.EscapeOneLineReviewModel
import com.hshim.apisis.api.escape.model.EscapeReviewParsingDetailScoreModel
import com.hshim.apisis.api.escape.model.EscapeReviewRecommendedModel
import com.hshim.apisis.api.escape.model.EscapeReviewRequest
import com.hshim.apisis.api.escape.repository.EscapeReviewRepository
import com.hshim.apisis.api.escape.repository.EscapeThemeRepository
import com.hshim.apisis.properties.EscapeReviewProperties
import io.clroot.excel.parser.ParseResult
import io.clroot.excel.parser.parseExcel
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.net.URI

@Service
@Transactional
class EscapeReviewCommandService(
    private val escapeThemeRepository: EscapeThemeRepository,
    private val escapeReviewRepository: EscapeReviewRepository,
    private val escapeReviewProperties: EscapeReviewProperties,
) {
    private val log: Logger = LoggerFactory.getLogger(EscapeReviewCommandService::class.java)

    fun parsing() {
        val excelBytes = URI.create(escapeReviewProperties.url).toURL().openStream().use { it.readBytes() }

        // 추천도 순 정렬
        val recommended = parseExcel<EscapeReviewRecommendedModel>(excelBytes.inputStream()) {
            sheetIndex = 1
            headerRow = 1
        }
        val requestMap = when (recommended) {
            is ParseResult.Success -> recommended.data.map { it.toRequest() }.associateBy { it.no }
            is ParseResult.Failure -> {
                log.error("escape review recommended parsing failed")
                return
            }
        }

        // 추천도 세부 평점
        val detailScore = parseExcel<EscapeReviewParsingDetailScoreModel>(excelBytes.inputStream()) {
            sheetIndex = 3
            headerRow = 1
        }
        when (detailScore) {
            is ParseResult.Success -> detailScore.data.forEach { requestMap[it.no]?.updateToDetailScore(it) }
            is ParseResult.Failure -> {
                log.error("escape review detail score parsing failed")
                return
            }
        }

        // 한줄평
        val oneLineReview = parseExcel<EscapeOneLineReviewModel>(excelBytes.inputStream()) {
            sheetIndex = 4
            headerRow = 1
        }
        when (oneLineReview) {
            is ParseResult.Success -> oneLineReview.data.forEach { requestMap[it.no]?.updateToOneLine(it) }
            is ParseResult.Failure -> {
                log.error("escape one line review parsing failed")
                return
            }
        }

        deleteAll()
        init(requestMap.values.toList())
    }

    fun init(requests: List<EscapeReviewRequest>) {
        requests.forEach {
            val theme = escapeThemeRepository.findTopByNameContains(it.themeName.replace(" ", ""))
            if (theme == null) {
                log.info("escape theme '${it.no} ${it.themeName}' not found")
                return@forEach
            }
            escapeReviewRepository.save(it.toEntity(theme))
        }
    }

    fun deleteAll() {
        escapeReviewRepository.deleteAll()
    }
}