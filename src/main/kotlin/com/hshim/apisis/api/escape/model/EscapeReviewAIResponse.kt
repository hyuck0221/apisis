package com.hshim.apisis.api.escape.model

import com.hshim.kemi.annotation.GeminiField

data class EscapeReviewAIResponse (
    @GeminiField("""
        매핑 정보
        key:refId, value:no 형태의 Map들이 해당 object 안에 나열됨.
        ex) "mapping": {
            "1000": "[30]",
            "1001": "[40]"
        }
    """)
    val mapping: Map<String, String>,

    @GeminiField("""
        매핑 실패한 데이터의 상세정보
        매핑에 실패한 데이터들은 다시 DB에서 검색해볼 수 있도록 아래 정보들을 추가 제공한다.
        - no: 번호 (ex."[30]")
        - en: 영문 번역 (ex."escape")
        - ko: 한글 번역 (ex."탈출")
    """)
    val failInfos: List<FailInfo>,
) {
    data class FailInfo (
        val no: String,
        val en: String,
        val ko: String,
    )
}