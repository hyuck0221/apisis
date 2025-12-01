package com.hshim.apisis.auth.entity

import jakarta.persistence.*
import util.CommonUtil.ulid
import java.time.LocalDateTime

@Entity
@Table(
    name = "api_call_log",
    indexes = [
        Index(name = "idx_api_key_url_method", columnList = "api_key_value, url, method")
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
    val calledAt: LocalDateTime = LocalDateTime.now()
)
