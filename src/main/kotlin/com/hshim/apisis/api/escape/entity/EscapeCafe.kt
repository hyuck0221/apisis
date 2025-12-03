package com.hshim.apisis.api.escape.entity

import com.hshim.apisis.api.base.entity.BaseTimeEntity
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import util.CommonUtil.ulid

@Entity
@Table(name = "escape_cafe")
data class EscapeCafe(
    @Id
    val id: String = ulid(),

    @Column(nullable = false)
    var name: String,

    @Column(nullable = false)
    var location: String,

    @Column(nullable = false)
    var area: String,

    @Column(nullable = false)
    var isOpen: Boolean,

    @Column(nullable = false)
    var lat: Double,

    @Column(nullable = false)
    var lng: Double,

    @Column(nullable = false)
    var address: String,

    @Column(nullable = false)
    var phoneNo: String,

    @Column(nullable = false)
    var homepage: String,

    ) : BaseTimeEntity() {
    companion object {
        fun of(id: String) = EscapeCafe(
            id = id,
            name = "",
            location = "",
            area = "",
            isOpen = false,
            lat = 0.0,
            lng = 0.0,
            address = "",
            phoneNo = "",
            homepage = "",
        )
    }
}