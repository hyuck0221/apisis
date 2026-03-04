package com.hshim.apisis.api.url.service

import com.google.zxing.BarcodeFormat
import com.google.zxing.client.j2se.MatrixToImageWriter
import com.google.zxing.qrcode.QRCodeWriter
import com.hshim.apisis.api.url.model.*
import com.hshim.apisis.properties.ShortUrlProperties
import org.springframework.http.HttpEntity
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate
import org.springframework.web.server.ResponseStatusException
import java.io.ByteArrayOutputStream
import java.util.*

@Service
class UrlQueryService(private val shortUrlProperties: ShortUrlProperties) {

    val restTemplate = RestTemplate()

    fun urlShorter(request: UrlRequest): UrlResponse {

        val result = restTemplate.exchange(
            shortUrlProperties.url + "/api/short",
            HttpMethod.POST,
            HttpEntity(request),
            UrlOpenAPIResponse::class.java
        ).body?.result ?: throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "URL shortening failed")

        return UrlResponse(shortUrlProperties.url + "/$result")
    }

    fun generateQR(request: QRRequest): QrResponse {
        val bitMatrix = QRCodeWriter().encode(request.url, BarcodeFormat.QR_CODE, request.x ?: 300, request.y ?: 300)
        val bytes =
            ByteArrayOutputStream().also { MatrixToImageWriter.writeToStream(bitMatrix, "PNG", it) }.toByteArray()
        return QrResponse(Base64.getEncoder().encodeToString(bytes))
    }
}