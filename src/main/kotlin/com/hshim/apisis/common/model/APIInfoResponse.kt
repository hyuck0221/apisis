package com.hshim.apisis.common.model

import com.hshim.apisis.common.annotation.Information

data class APIInfoResponse (
    val url: String,
    val method: String,
    val category: String,
    val title: String,
    val description: String,
    val version: String,
    val callLimit: Long,
    val requestSchema: Map<String, Any>,
    val responseSchema: Map<String, Any>,
    val requestInfos: List<FieldInfo>,
    val responseInfos: List<FieldInfo>
) {
    constructor(
        url: String,
        method: String,
        information: Information,
        requestSchema: Map<String, Any>,
        responseSchema: Map<String, Any>,
        requestInfos: List<FieldInfo>,
        responseInfos: List<FieldInfo>
    ): this (
        url = url,
        method = method,
        category = information.category,
        title = information.title,
        description = information.description,
        version = information.version,
        callLimit = information.callLimit,
        requestSchema = requestSchema,
        responseSchema = responseSchema,
        requestInfos = requestInfos,
        responseInfos = responseInfos
    )
}