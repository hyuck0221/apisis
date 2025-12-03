package com.hshim.apisis.api.escape.model

class EscapeCafeBoundsSearchCondition(
    val minLat: Double,
    val maxLat: Double,
    val minLng: Double,
    val maxLng: Double,
    val onlyOpen: Boolean = false,
)