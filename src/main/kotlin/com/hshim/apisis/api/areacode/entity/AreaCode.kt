package com.hshim.apisis.api.areacode.entity

import com.hshim.apisis.api.base.entity.BaseTimeEntity
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name = "area_code")
data class AreaCode(

    @Id
    @Column(nullable = false)
    var code: String,

    @Column(nullable = false)
    var address: String,

    @Column(nullable = false)
    var city: String,

    @Column(nullable = true)
    var town: String?,

    @Column(nullable = true)
    var village: String?,

) : BaseTimeEntity()