package com.hshim.apisis.api.url.entity

import com.hshim.apisis.api.base.entity.BaseTimeEntity
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Index
import jakarta.persistence.Table


@Entity
@Table(name = "url_short", indexes = [Index(name = "idx_url_short_base_url", columnList = "baseUrl")])
class UrlShort(
    @Id
    @Column(nullable = false, columnDefinition = "VARCHAR(255) COLLATE utf8mb4_bin")
    val id: String,

    @Column(nullable = false, columnDefinition = "TEXT")
    val baseUrl: String,

    @Column(nullable = false)
    var view: Long,
) : BaseTimeEntity() {

    constructor(top: UrlShort?, baseUrl: String): this (
        id = top?.id?.let { generateId(it) } ?: "0",
        baseUrl = baseUrl,
        view = 0,
    )

    companion object {
        private const val CHARS = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ"

        fun generateId(topId: String): String {
            if (topId.isEmpty()) return CHARS[0].toString()

            val chars = topId.map { CHARS.indexOf(it) }.toIntArray()

            for (i in chars.indices.reversed()) {
                if (chars[i] < CHARS.length - 1) {
                    chars[i]++
                    return chars.map { CHARS[it] }.joinToString("")
                }
                chars[i] = 0
            }

            return CHARS[0] + chars.map { CHARS[it] }.joinToString("")
        }
    }
}