package com.hshim.apisis.api.escape.model

class EscapeThemeOpenAPIRequest(
    val q: String?,
    val limit: Int,
    val offset: Int,
    var filter: List<String>,
    var sort: List<String>,
) {
    companion object {
        fun top() = EscapeThemeOpenAPIRequest(
            q = null,
            limit = 1,
            offset = 0,
            filter = emptyList(),
            sort = listOf("ref_id:desc"),
        )
    }
}