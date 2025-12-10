package com.hshim.apisis.license.entity

import com.hshim.apisis.api.base.entity.BaseTimeEntity
import com.hshim.apisis.user.enums.PaymentType
import jakarta.persistence.*
import org.hibernate.annotations.ColumnDefault
import java.time.LocalDateTime
import java.util.*

@Entity
@Table(name = "license")
class License(
    @Id
    val id: String = generateLicenseId(),

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    var paymentType: PaymentType,

    @Column(nullable = true)
    var expiredDate: LocalDateTime?,

): BaseTimeEntity() {
    companion object {
        fun generateLicenseId(): String {
            val raw = UUID.randomUUID().toString().replace("-", "")
            return raw.chunked(4).take(4).joinToString("-")
        }
    }
}