package com.hshim.apisis.common.model

import com.hshim.apisis.common.annotation.Information

data class APIInfoResponse (
    val url: String,
    val method: String,
    val category: String,
    val title: String,
    val description: String,
    val version: String,
    val callLimitFree: String,
    val callLimitBasic: String,
    val callLimitPro: String,
    val requestSchema: Map<String, Any>,
    val responseSchema: Any,
    val requestInfos: List<FieldInfo>,
    val responseInfos: List<FieldInfo>
) {
    constructor(
        url: String,
        method: String,
        information: Information,
        requestSchema: Map<String, Any>,
        responseSchema: Any,
        requestInfos: List<FieldInfo>,
        responseInfos: List<FieldInfo>
    ): this (
        url = url,
        method = method,
        category = information.category,
        title = information.title,
        description = information.description,
        version = information.version,
        callLimitFree = if (information.callLimitFree == -1L) "무제한" else information.callLimitFree.toString(),
        callLimitBasic = if (information.callLimitBasic == -1L) "무제한" else information.callLimitBasic.toString(),
        callLimitPro = if (information.callLimitPro == -1L) "무제한" else information.callLimitPro.toString(),
        requestSchema = requestSchema,
        responseSchema = responseSchema,
        requestInfos = requestInfos,
        responseInfos = responseInfos
    )
}