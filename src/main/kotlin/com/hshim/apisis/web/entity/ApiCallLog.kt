package com.hshim.apisis.web.entity

import jakarta.persistence.*
import util.CommonUtil.ulid
import java.time.LocalDateTime

@Entity
@Table(
    name = "api_call_log",
    indexes = [
        Index(name = "idx_api_key_value", columnList = "api_key_value"),
        Index(name = "idx_called_at", columnList = "called_at"),
        Index(name = "idx_api_key_success", columnList = "api_key_value, is_success")
    ]
)
class ApiCallLog(
    @Id
    val id: String = ulid(),

    @Column(nullable = false, length = 64)
    val apiKeyValue: String,

    @Column(nullable = false, length = 500)
    val url: String,

    @Column(nullable = false, length = 10)
    val method: String,

    @Column(nullable = false)
    val responseTimeMs: Long,

    @Column(nullable = false)
    val isSuccess: Boolean,

    @Column(nullable = true)
    val httpStatus: Int? = null,

    @Column(nullable = true, length = 1000)
    val errorMessage: String? = null,

    @Column(nullable = false)
    val calledAt: LocalDateTime = LocalDateTime.now()
)
