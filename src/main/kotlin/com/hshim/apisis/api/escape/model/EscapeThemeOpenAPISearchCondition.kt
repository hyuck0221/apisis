package com.hshim.apisis.api.escape.model

import org.springframework.data.domain.Pageable

class EscapeThemeOpenAPISearchCondition(
    val search: String? = null,
    val location: String? = null,
    val area: String? = null,
) {
    val filters = mutableListOf<String>()
    val sorts = mutableListOf<String>()

    init {
        if (area != null) filters.add("area = \"$area\"")
        if (location != null) filters.add("location = \"$location\"")

        sorts.add("ref_id:desc")
    }

    fun toOpenAPIRequest(pageable: Pageable) = EscapeThemeOpenAPIRequest(
        q = search,
        limit = pageable.pageSize,
        offset = 0,
        filter = filters + buildPageFilter(pageable),
        sort = sorts,
    )

    fun buildPageFilter(pageable: Pageable): List<String> {
        val start = pageable.pageSize * pageable.pageNumber
        val end = start + pageable.pageSize
        return listOf("ref_id>$start", "ref_id<=$end")
    }
}