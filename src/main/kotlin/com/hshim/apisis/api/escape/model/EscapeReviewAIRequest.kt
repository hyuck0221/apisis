package com.hshim.apisis.api.escape.model

import com.hshim.apisis.web.enums.Prompt
import util.ClassUtil.classToJson

data class EscapeReviewAIRequest(
    val cafes: List<EscapeCafeResponse>,
    val needMappingThemes: List<EscapeReviewRequest>,
) {
    fun toPrompt() = Prompt.ESCAPE_REVIEW_PARSING.message + "\n데이터 제공: \n${this.classToJson()}\n"
}