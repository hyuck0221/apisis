package com.hshim.apisis.api.areacode.model

import com.hshim.apisis.api.areacode.entity.AreaCode
import io.clroot.excel.annotation.Column
import io.clroot.excel.annotation.Excel

@Excel
data class AreaCodeExcelInfo(
    @Column("code") val code: String,
    @Column("city") val city: String,
    @Column("town") val town: String?,
    @Column("village") val village: String?,
) {
    fun toEntity() = AreaCode (
        code = code,
        address = "$city ${town ?: ""} ${village ?: ""}",
        city = city,
        town = town,
        village = village,
    )
}