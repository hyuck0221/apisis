package com.hshim.apisis.common.model

import com.hshim.apisis.common.annotation.Information
import java.time.LocalDateTime

data class Envelope<T>(
    val title: String,
    val version: String,
    val current: Long,
    val limit: Long?,
    val timestamp: String,
    val payload: T?,
    val processMs: Long,
) {
    constructor(information: Information, current: Long, payload: T?, responseTimeMs: Long) : this(
        title = information.title,
        version = information.version,
        current = current,
        limit = information.callLimit.takeIf { it > 0 },
        timestamp = LocalDateTime.now().toString(),
        payload = payload,
        processMs = responseTimeMs,
    )
}