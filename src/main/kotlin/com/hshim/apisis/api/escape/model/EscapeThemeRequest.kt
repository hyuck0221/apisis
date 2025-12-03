package com.hshim.apisis.api.escape.model

import com.hshim.apisis.api.escape.entity.EscapeCafe
import com.hshim.apisis.api.escape.entity.EscapeTheme

class EscapeThemeRequest(
    val refId: Long,
    val escapeCafeId: String,
    val name: String,
    val description: String?,
    val isOpen: Boolean,
    val photoUrl: String,
    val playtime: Int,
    val price: Int,
    val difficulty: Double,
    val fear: Double,
    val activity: Double,
    val satisfy: Double,
    val problem: Double,
    val story: Double,
    val interior: Double,
    val act: Double,
) {
    constructor(photoBaseUrl: String, hit: EscapeThemeOpenAPIResponse.Hit, cafeId: String): this (
        refId = hit.ref_id,
        escapeCafeId = cafeId,
        name = hit.title,
        description = hit.description,
        isOpen = hit.isopen,
        photoUrl = "$photoBaseUrl/${hit.thumb_loc}",
        playtime = hit.playtime,
        price = hit.price ?: 0,
        difficulty = hit.difficultyTotalRating ?: 0.0,
        fear = hit.fearTotalRating ?: 0.0,
        activity = hit.activityTotalRating ?: 0.0,
        satisfy = hit.satisfyTotalRating ?: 0.0,
        problem = hit.problemTotalRating ?: 0.0,
        story = hit.storyTotalRating ?: 0.0,
        interior = hit.interiorTotalRating ?: 0.0,
        act = hit.actTotalRating ?: 0.0,
    )

    fun toEntity() = EscapeTheme(
        refId = this.refId,
        escapeCafe = EscapeCafe.of(this.escapeCafeId),
        name = this.name,
        description = this.description,
        isOpen = this.isOpen,
        photoUrl = this.photoUrl,
        playtime = this.playtime,
        price = this.price,
        difficulty = this.difficulty,
        fear = this.fear,
        activity = this.activity,
        satisfy = this.satisfy,
        problem = this.problem,
        story = this.story,
        interior = this.interior,
        act = this.act,
    )
}