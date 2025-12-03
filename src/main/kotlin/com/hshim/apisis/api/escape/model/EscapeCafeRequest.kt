package com.hshim.apisis.api.escape.model

import com.hshim.apisis.api.escape.entity.EscapeCafe

class EscapeCafeRequest(
    val name: String,
    val location: String,
    val area: String,
    val isOpen: Boolean,
    val lat: Double,
    val lng: Double,
    val address: String,
    val phoneNo: String,
    val homepage: String,
) {
    constructor(hit: EscapeThemeOpenAPIResponse.Hit) : this(
        name = hit.store_name,
        location = hit.location,
        area = hit.area,
        isOpen = hit.store_isopen,
        lat = hit._geo.lat,
        lng = hit._geo.lng,
        address = hit.address,
        phoneNo = hit.store_tel,
        homepage = hit.store_homepage,
    )

    fun toEntity() = EscapeCafe(
        name = this.name,
        location = this.location,
        area = this.area,
        isOpen = this.isOpen,
        lat = this.lat,
        lng = this.lng,
        address = this.address,
        phoneNo = this.phoneNo,
        homepage = this.homepage,
    )
}