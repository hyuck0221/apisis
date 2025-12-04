package com.hshim.apisis.api.escape.service

import com.hshim.apisis.api.escape.model.EscapeCafeRequest
import com.hshim.apisis.api.escape.model.EscapeThemeOpenAPISearchCondition
import com.hshim.apisis.api.escape.model.EscapeThemeRequest
import com.hshim.apisis.api.escape.repository.EscapeThemeRepository
import com.hshim.apisis.properties.EscapeCrawlingProperties
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class EscapeThemeCommandService(
    private val escapeCafeQueryService: EscapeCafeQueryService,
    private val escapeCafeCommandService: EscapeCafeCommandService,
    private val escapeThemeRepository: EscapeThemeRepository,
    private val escapeThemeQueryService: EscapeThemeQueryService,
    private val properties: EscapeCrawlingProperties,
) {
    fun init(requests: List<EscapeThemeRequest>) {
        val themeMap = escapeThemeRepository.findAllById(requests.map { it.refId }).associateBy { it.refId }
        requests.forEach { request ->
            if (!themeMap.contains(request.refId)) escapeThemeRepository.save(request.toEntity())
        }
    }

    fun migration() {
        val sliceCnt = 500

        val topHit = escapeThemeQueryService.findTopHitByOpenAPI()
        val topTheme = escapeThemeRepository.findTopByOrderByRefIdDesc()
        if (topHit.ref_id == topTheme?.refId) return

        var currentId = topTheme?.refId ?: 0
        while (currentId <= topHit.ref_id) {
            val pageable = PageRequest.of(currentId.toInt() / sliceCnt, sliceCnt)
            val condition = EscapeThemeOpenAPISearchCondition()
            val hits = escapeThemeQueryService.findAllHitPageByOpenAPI(condition, pageable).content

            val cafeMap = escapeCafeQueryService.findAllByNames(hits.map { it.store_name })
                .associateBy { it.name to it.location to it.area }

            val requests = hits.map { hit ->
                val cafeId = cafeMap[hit.store_name to hit.location to hit.area]?.id
                    ?: escapeCafeCommandService.init(EscapeCafeRequest(hit)).id
                EscapeThemeRequest(properties.photoBaseUrl, hit, cafeId)
            }
            init(requests)
            currentId += sliceCnt
        }
    }
}