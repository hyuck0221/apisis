package com.hshim.apisis.api.escape.model

import com.hshim.apisis.api.escape.entity.EscapeCafe

class EscapeCafeLocationResponse(
    val area: String,
    val location: String,
) {
    constructor(escapeCafe: EscapeCafe) : this(
        area = escapeCafe.area,
        location = escapeCafe.location,
    )
}