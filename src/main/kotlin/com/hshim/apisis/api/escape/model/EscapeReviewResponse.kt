package com.hshim.apisis.api.escape.model

import com.hshim.apisis.api.escape.entity.EscapeReview

data class EscapeReviewResponse (
    val average: Double?,
    val difficulty: String?,
    val satisfy: String?,
    val fear: Int?,
    val activity: Int?,
    val problem: Int?,
    val interior: Int?,
    val story: Int?,
    val idea: Int?,
    val act: Int?,
    val review: String?,
    val escapeTip: String?,
) {
    constructor(review: EscapeReview): this (
        average = review.average,
        difficulty = review.difficulty,
        satisfy = review.satisfy,
        fear = review.fear,
        activity = review.activity,
        problem = review.problem,
        interior = review.interior,
        story = review.story,
        idea = review.idea,
        act = review.act,
        review = review.review,
        escapeTip = review.escapeTip,
    )
}