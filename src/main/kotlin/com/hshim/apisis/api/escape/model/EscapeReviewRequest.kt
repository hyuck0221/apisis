package com.hshim.apisis.api.escape.model

import com.hshim.apisis.api.escape.entity.EscapeReview
import com.hshim.apisis.api.escape.entity.EscapeTheme

data class EscapeReviewRequest(
    var no: String,
    var cafeName: String,
    var themeName: String,
    var average: Double? = null,
    var difficulty: String? = null,
    var satisfy: String? = null,
    var fear: Int? = null,
    var activity: Int? = null,
    var problem: Int? = null,
    var interior: Int? = null,
    var story: Int? = null,
    var idea: Int? = null,
    var act: Int? = null,
    var review: String? = null,
    var escapeTip: String? = null,
) {
    fun updateToDetailScore(detailScoreModel: EscapeReviewParsingDetailScoreModel) {
        average = detailScoreModel.average?.replace("?", "0.0")?.toDouble()
        problem = detailScoreModel.problem?.replace("-", "0")?.toInt()
        interior = detailScoreModel.interior?.replace("-", "0")?.toInt()
        story = detailScoreModel.story?.replace("-", "0")?.toInt()
        idea = detailScoreModel.idea?.replace("-", "0")?.toInt()
        act = detailScoreModel.act?.replace("-", "0")?.toInt()
    }

    fun updateToOneLine(oneLineModel: EscapeOneLineReviewModel) {
        review = oneLineModel.review
        escapeTip = oneLineModel.escapeTip
    }

    fun toEntity(escapeTheme: EscapeTheme) = EscapeReview(
        escapeTheme = escapeTheme,
        average = average,
        difficulty = difficulty,
        satisfy = satisfy,
        fear = fear,
        activity = activity,
        problem = problem,
        interior = interior,
        story = story,
        idea = idea,
        act = act,
        review = review,
        escapeTip = escapeTip,
    )
}