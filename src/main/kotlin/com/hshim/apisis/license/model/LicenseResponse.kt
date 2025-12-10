package com.hshim.apisis.license.model

import com.hshim.apisis.license.entity.License

data class LicenseResponse(
    val id: String,
    val paymentType: String,
    val expiredDate: String?,
) {
    constructor(license: License) : this(
        id = license.id,
        paymentType = license.paymentType.name,
        expiredDate = license.expiredDate?.toString(),
    )
}
