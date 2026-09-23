package com.hshim.apisis.mcp.controller

import com.hshim.apisis.mcp.service.McpService
import jakarta.servlet.http.HttpServletRequest
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.net.URI

@RestController
@RequestMapping("/mcp")
class McpController(
    private val mcpService: McpService,
    @Value("\${apisis.mcp.allowed-origins:\${apisis.base-url:https://apisis.dev}}")
    private val allowedOrigins: String
) {
    private val logger = LoggerFactory.getLogger(McpController::class.java)

    @PostMapping(consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun handleMessage(
        @RequestHeader headers: HttpHeaders,
        @RequestBody requestBody: Map<String, Any?>,
        request: HttpServletRequest
    ): ResponseEntity<Any> {
        if (!hasValidOrigin(request)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build()
        }

        if (!supportsRequiredResponseTypes(headers)) {
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).build()
        }

        val method = requestBody["method"] as? String
        val requestId = requestBody["id"]
        val protocolVersion = requestProtocolVersion(requestBody)

        if (requestBody["jsonrpc"] != "2.0") {
            return jsonRpcError(HttpStatus.BAD_REQUEST, requestId, -32600, "Invalid JSON-RPC request")
        }

        if (protocolVersion == MODERN_PROTOCOL_VERSION) {
            val headerError = validateModernHeaders(headers, requestBody)
            if (headerError != null) {
                return jsonRpcError(HttpStatus.BAD_REQUEST, requestId, -32020, headerError)
            }
            if (method == "initialize") {
                return jsonRpcError(HttpStatus.NOT_FOUND, requestId, -32601, "Method not found: $method")
            }
        } else if (headers.getFirst(PROTOCOL_VERSION_HEADER) == MODERN_PROTOCOL_VERSION) {
            return jsonRpcError(
                HttpStatus.BAD_REQUEST,
                requestId,
                -32020,
                "MCP-Protocol-Version must match params._meta.io.modelcontextprotocol/protocolVersion"
            )
        } else {
            val headerVersion = headers.getFirst(PROTOCOL_VERSION_HEADER)
            if (headerVersion != null && headerVersion !in LEGACY_PROTOCOL_VERSIONS) {
                return jsonRpcError(
                    HttpStatus.BAD_REQUEST,
                    requestId,
                    -32022,
                    "Unsupported protocol version: $headerVersion"
                )
            }
        }

        if (method == null) {
            return if (requestBody.containsKey("result") || requestBody.containsKey("error")) {
                accepted()
            } else {
                jsonRpcError(HttpStatus.BAD_REQUEST, requestId, -32600, "Invalid JSON-RPC request")
            }
        }

        if (!requestBody.containsKey("id")) {
            return try {
                mcpService.handleMessage(requestBody)
                accepted()
            } catch (e: Exception) {
                logger.error("Error handling MCP notification: method=$method", e)
                ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build()
            }
        }

        return try {
            val response = mcpService.handleMessage(requestBody)
                ?: return accepted()
            val status = if (protocolVersion == MODERN_PROTOCOL_VERSION && isMethodNotFound(response)) {
                HttpStatus.NOT_FOUND
            } else {
                HttpStatus.OK
            }
            ResponseEntity.status(status)
                .contentType(MediaType.APPLICATION_JSON)
                .body(response)
        } catch (e: Exception) {
            logger.error("Error handling MCP request: method=$method, id=$requestId", e)
            jsonRpcError(HttpStatus.INTERNAL_SERVER_ERROR, requestId, -32603, "Internal error")
        }
    }

    @GetMapping
    fun getNotSupported(request: HttpServletRequest): ResponseEntity<Any> =
        if (hasValidOrigin(request)) methodNotAllowed() else ResponseEntity.status(HttpStatus.FORBIDDEN).build()

    @DeleteMapping
    fun deleteNotSupported(request: HttpServletRequest): ResponseEntity<Any> =
        if (hasValidOrigin(request)) methodNotAllowed() else ResponseEntity.status(HttpStatus.FORBIDDEN).build()

    private fun supportsRequiredResponseTypes(headers: HttpHeaders): Boolean {
        val acceptHeader = headers.getFirst(HttpHeaders.ACCEPT) ?: return false
        val acceptedTypes = try {
            MediaType.parseMediaTypes(acceptHeader)
        } catch (_: IllegalArgumentException) {
            return false
        }
        return acceptedTypes.any { it.qualityValue > 0 && it.isCompatibleWith(MediaType.APPLICATION_JSON) } &&
            acceptedTypes.any { it.qualityValue > 0 && it.isCompatibleWith(MediaType.TEXT_EVENT_STREAM) }
    }

    private fun hasValidOrigin(request: HttpServletRequest): Boolean {
        val origin = request.getHeader(HttpHeaders.ORIGIN) ?: return true
        if (origin == "null") return false

        return try {
            val originUri = URI(origin)
            (originUri.scheme == "http" || originUri.scheme == "https") &&
                originUri.host != null &&
                originUri.userInfo == null &&
                originUri.path.isNullOrEmpty() &&
                originUri.query == null &&
                originUri.fragment == null &&
                allowedOrigins.split(',').any { allowedOrigin ->
                    runCatching { URI(allowedOrigin.trim()) }.getOrNull()?.let { configuredOrigin ->
                        originUri.scheme.equals(configuredOrigin.scheme, ignoreCase = true) &&
                            originUri.host.equals(configuredOrigin.host, ignoreCase = true) &&
                            effectivePort(originUri) == effectivePort(configuredOrigin)
                    } == true
                }
        } catch (_: Exception) {
            false
        }
    }

    private fun effectivePort(uri: URI): Int = when {
        uri.port != -1 -> uri.port
        uri.scheme.equals("https", ignoreCase = true) -> 443
        else -> 80
    }

    private fun requestProtocolVersion(requestBody: Map<String, Any?>): String? {
        val params = requestBody["params"] as? Map<*, *> ?: return null
        val meta = params["_meta"] as? Map<*, *> ?: return null
        return meta[PROTOCOL_VERSION_META_KEY] as? String
    }

    private fun validateModernHeaders(headers: HttpHeaders, requestBody: Map<String, Any?>): String? {
        if (headers.getFirst(PROTOCOL_VERSION_HEADER) != MODERN_PROTOCOL_VERSION) {
            return "Missing or mismatched MCP-Protocol-Version header"
        }

        val method = requestBody["method"] as? String
            ?: return "MCP requests must include a method"
        if (headers.getFirst(METHOD_HEADER) != method) {
            return "Missing or mismatched Mcp-Method header"
        }

        val params = requestBody["params"] as? Map<*, *>
            ?: return "MCP requests must include params._meta"
        val meta = params["_meta"] as? Map<*, *>
            ?: return "MCP requests must include params._meta"
        if (meta[CLIENT_CAPABILITIES_META_KEY] !is Map<*, *>) {
            return "Missing or invalid io.modelcontextprotocol/clientCapabilities"
        }

        if (method == "tools/call") {
            val name = params["name"] as? String
                ?: return "tools/call requires params.name"
            val headerName = headers.getFirst(NAME_HEADER)
                ?: return "Missing Mcp-Name header"
            if (decodeHeaderValue(headerName) != name) {
                return "Mcp-Name header does not match params.name"
            }
        }

        return null
    }

    private fun decodeHeaderValue(value: String): String? {
        if (!value.startsWith(BASE64_PREFIX) || !value.endsWith(BASE64_SUFFIX)) return value
        return try {
            val encoded = value.removePrefix(BASE64_PREFIX).removeSuffix(BASE64_SUFFIX)
            String(java.util.Base64.getDecoder().decode(encoded), Charsets.UTF_8)
        } catch (_: IllegalArgumentException) {
            null
        }
    }

    private fun isMethodNotFound(response: Map<String, Any?>): Boolean {
        val error = response["error"] as? Map<*, *> ?: return false
        return error["code"] == -32601
    }

    private fun jsonRpcError(
        status: HttpStatus,
        id: Any?,
        code: Int,
        message: String
    ): ResponseEntity<Any> = ResponseEntity.status(status)
        .contentType(MediaType.APPLICATION_JSON)
        .body(
            mapOf(
                "jsonrpc" to "2.0",
                "id" to id,
                "error" to mapOf("code" to code, "message" to message)
            )
        )

    private fun accepted(): ResponseEntity<Any> = ResponseEntity.status(HttpStatus.ACCEPTED).build()

    private fun methodNotAllowed(): ResponseEntity<Any> = ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED)
        .header(HttpHeaders.ALLOW, "POST")
        .build()

    companion object {
        private const val MODERN_PROTOCOL_VERSION = "2026-07-28"
        private const val PROTOCOL_VERSION_HEADER = "MCP-Protocol-Version"
        private const val PROTOCOL_VERSION_META_KEY = "io.modelcontextprotocol/protocolVersion"
        private const val CLIENT_CAPABILITIES_META_KEY = "io.modelcontextprotocol/clientCapabilities"
        private const val METHOD_HEADER = "Mcp-Method"
        private const val NAME_HEADER = "Mcp-Name"
        private const val BASE64_PREFIX = "=?base64?"
        private const val BASE64_SUFFIX = "?="
        private val LEGACY_PROTOCOL_VERSIONS = setOf("2025-11-25", "2025-06-18", "2025-03-26")
    }
}
