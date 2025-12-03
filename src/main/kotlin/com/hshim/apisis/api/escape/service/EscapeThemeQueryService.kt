package com.hshim.apisis.api.escape.service

import com.hshim.apisis.api.escape.entity.EscapeCafe
import com.hshim.apisis.api.escape.entity.EscapeTheme
import com.hshim.apisis.api.escape.model.EscapeThemeOpenAPIRequest
import com.hshim.apisis.api.escape.model.EscapeThemeOpenAPIResponse
import com.hshim.apisis.api.escape.model.EscapeThemeOpenAPISearchCondition
import com.hshim.apisis.api.escape.model.EscapeThemeSearchCondition
import com.hshim.apisis.api.escape.repository.EscapeThemeRepository
import com.hshim.apisis.properties.EscapeCrawlingProperties
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.client.RestTemplate
import org.springframework.web.server.ResponseStatusException

@Service
@Transactional(readOnly = true)
class EscapeThemeQueryService(
    private val escapeThemeRepository: EscapeThemeRepository,
    private val properties: EscapeCrawlingProperties,
) {
    private val restTemplate = RestTemplate()

    fun findAllByCafes(cafes: List<EscapeCafe>): List<EscapeTheme> {
        return escapeThemeRepository.findAllByEscapeCafeIn(cafes)
    }

    fun findAllPageBy(condition: EscapeThemeSearchCondition, pageable: Pageable): Page<EscapeTheme> {
        return escapeThemeRepository.findAllByCondition(
            search = condition.search,
            onlyOpen = condition.onlyOpen,
            areas = condition.areas.takeIf { it.isNotEmpty() },
            locations = condition.locations.takeIf { it.isNotEmpty() },
            startPlaytime = condition.startPlaytime,
            endPlaytime = condition.endPlaytime,
            startPrice = condition.startPrice,
            endPrice = condition.endPrice,
            startDifficulty = condition.startDifficulty,
            endDifficulty = condition.endDifficulty,
            startFear = condition.startFear,
            endFear = condition.endFear,
            startActivity = condition.startActivity,
            endActivity = condition.endActivity,
            startSatisfy = condition.startSatisfy,
            endSatisfy = condition.endSatisfy,
            startProblem = condition.startProblem,
            endProblem = condition.endProblem,
            startStory = condition.startStory,
            endStory = condition.endStory,
            startInterior = condition.startInterior,
            endInterior = condition.endInterior,
            startAct = condition.startAct,
            endAct = condition.endAct,
            pageable = pageable,
        )
    }

    fun findTopHitByOpenAPI(): EscapeThemeOpenAPIResponse.Hit {
        val headers = HttpHeaders().apply { set("Authorization", properties.crawlingAuthorization) }
        val url = properties.crawlingUrl
        val body = EscapeThemeOpenAPIRequest.top()

        return restTemplate.exchange(
            url,
            HttpMethod.POST,
            HttpEntity(body, headers),
            EscapeThemeOpenAPIResponse::class.java
        ).body?.hits?.first()
            ?: throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "API call failed")
    }

    fun findAllHitPageByOpenAPI(
        condition: EscapeThemeOpenAPISearchCondition,
        pageable: Pageable,
    ): Page<EscapeThemeOpenAPIResponse.Hit> {

        val headers = HttpHeaders().apply { set("Authorization", properties.crawlingAuthorization) }
        val url = properties.crawlingUrl + if (condition.search == null) "" else "?search=${condition.search}"
        val body = condition.toOpenAPIRequest(pageable)

        val result = restTemplate.exchange(
            url,
            HttpMethod.POST,
            HttpEntity(body, headers),
            EscapeThemeOpenAPIResponse::class.java
        ).body?.hits ?: emptyList()

        return PageImpl(result, pageable, findTopHitByOpenAPI().ref_id)
    }
}