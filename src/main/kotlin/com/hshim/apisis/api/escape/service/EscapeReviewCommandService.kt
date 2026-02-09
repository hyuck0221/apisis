package com.hshim.apisis.api.escape.service

import com.hshim.apisis.api.escape.entity.EscapeReview
import com.hshim.apisis.api.escape.model.EscapeCafeResponse
import com.hshim.apisis.api.escape.model.EscapeOneLineReviewModel
import com.hshim.apisis.api.escape.model.EscapeReviewAIRequest
import com.hshim.apisis.api.escape.model.EscapeReviewAIResponse
import com.hshim.apisis.api.escape.model.EscapeReviewParsingDetailScoreModel
import com.hshim.apisis.api.escape.model.EscapeReviewRecommendedModel
import com.hshim.apisis.api.escape.model.EscapeReviewRequest
import com.hshim.apisis.api.escape.repository.EscapeCafeRepository
import com.hshim.apisis.api.escape.repository.EscapeReviewRepository
import com.hshim.apisis.api.escape.repository.EscapeThemeRepository
import com.hshim.apisis.properties.EscapeReviewProperties
import com.hshim.kemi.GeminiGenerator
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
    private val escapeThemeQueryService: EscapeThemeQueryService,
    private val escapeThemeRepository: EscapeThemeRepository,
    private val escapeReviewRepository: EscapeReviewRepository,
    private val escapeCafeRepository: EscapeCafeRepository,
    private val escapeReviewProperties: EscapeReviewProperties,
    private val geminiGenerator: GeminiGenerator,
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

        val aiInitRequests = mutableListOf<EscapeReviewRequest>()
        var successCnt = 0

        requests.forEach {

            // 1. 카페명 + 테마명 일치 (띄어쓰기 제거)
            var theme = escapeThemeRepository.findByEscapeCafeNameAndName(it.cafeName, it.themeName)
                ?.apply { log.info("success matching LEVEL 1 :: cafeName(${this.escapeCafe.name}:${it.cafeName}) themeName(${this.name}:${it.themeName})") }

            // 2. location(홍대, 강남 등) + 테마명 일치 (띄어쓰기 제거)
            if (theme == null) theme = escapeThemeRepository.findByEscapeCafeLocationAndName(it.location, it.themeName)
                ?.apply { log.info("success matching LEVEL 2 :: location(${this.escapeCafe.location}:${it.location}) themeName(${this.name}:${it.themeName})") }

            // 3. 테마명 일치 (띄어쓰기 제거)
            if (theme == null) theme = escapeThemeRepository.findTopByName(it.themeName)
                ?.apply { log.info("success matching LEVEL 3 :: themeName(${this.name}:${it.themeName})") }

            if (theme == null) {
                aiInitRequests.add(it)
                log.info("escape theme ${it.no} '${it.themeName}' not found. change ai search mode.")
                return@forEach
            }

            escapeReviewRepository.save(it.toEntity(theme))
            successCnt++
        }

        // 4. 카페명 + AI 서칭 테마명 일치
        // 5. AI 번역 테마명 일치
        successCnt += initByAI(aiInitRequests).size
        log.info("total:${requests.size} successCnt:$successCnt failCnt:$${requests.size - successCnt}")
    }

    fun initByAI(requests: List<EscapeReviewRequest>): List<EscapeReview> {
        if (requests.isEmpty()) return emptyList()
        val cafes = requests.map { it.cafeName }.toSet().flatMap { escapeCafeRepository.findAllByNameContains(it) }
        val themes = escapeThemeQueryService.findAllByCafes(cafes)
        val cafeToThemes = themes.groupBy { it.escapeCafe }
        val refIdToTheme = themes.associateBy { it.refId }
        val cafeResponses = cafes.map { EscapeCafeResponse(it, cafeToThemes[it] ?: emptyList()) }
        val aiRequest = EscapeReviewAIRequest(cafeResponses, requests)

        log.info("starting AI Request (total:${requests.size})")
        val (mapping, failInfos) =
            geminiGenerator.askWithClass<EscapeReviewAIResponse>(aiRequest.toPrompt()) ?: return emptyList()

        val successNos = mutableListOf<String>()

        // 4. 카페명 + AI 서칭 테마명 일치
        val reviews = mapping.mapNotNull { (refId, no) ->
            val theme = refIdToTheme[refId.toLong()] ?: return@mapNotNull null
            val request = requests.find { it.no == no } ?: return@mapNotNull null
            log.info("success matching LEVEL 4 :: themeName(${theme.name}:${request.themeName})")
            successNos.add(no)
            request.toEntity(theme)
        }.toMutableList()

        // 5. AI 번역 테마명 일치
        reviews += failInfos.mapNotNull { (no, en, ko) ->
            val theme = escapeThemeRepository.findTopByNameOr(en, ko) ?: return@mapNotNull null
            val request = requests.find { it.no == no } ?: return@mapNotNull null
            log.info("success matching LEVEL 5 :: themeName(${theme.name}:${request.themeName})")
            successNos.add(no)
            request.toEntity(theme)
        }

        requests.filterNot { successNos.contains(it.no) }
            .forEach { log.info("escape theme ${it.no} '${it.themeName}' not found.") }

        return escapeReviewRepository.saveAll(reviews)
    }

    fun deleteAll() {
        escapeReviewRepository.deleteAll()
    }
}