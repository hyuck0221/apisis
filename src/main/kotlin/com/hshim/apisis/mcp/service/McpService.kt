package com.hshim.apisis.mcp.service

import com.fasterxml.jackson.databind.ObjectMapper
import com.hshim.apisis.common.util.APIInfoComponent
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class McpService(
    private val apiInfoComponent: APIInfoComponent,
    private val objectMapper: ObjectMapper
) {
    private val logger = LoggerFactory.getLogger(McpService::class.java)

    fun handleMessage(requestBody: Map<String, Any?>): Map<String, Any?>? {
        val method = requestBody["method"] as? String ?: return null
        val id = requestBody["id"]
        val modernProtocol = requestProtocolVersion(requestBody) == MODERN_PROTOCOL_VERSION

        logger.info("MCP Request: method=$method, id=$id")

        return when (method) {
            "initialize" -> {
                mapOf(
                    "jsonrpc" to "2.0",
                    "id" to id,
                    "result" to mapOf(
                        "protocolVersion" to negotiatedLegacyProtocolVersion(requestBody),
                        "capabilities" to mapOf(
                            "tools" to mapOf("listChanged" to false)
                        ),
                        "serverInfo" to mapOf(
                            "name" to "Apisis MCP Server",
                            "version" to "1.0.0"
                        )
                    )
                )
            }
            "notifications/initialized" -> null
            "ping" -> rpcResult(id, emptyMap(), modernProtocol)
            "server/discover" -> {
                if (!modernProtocol) {
                    errorResponse(id, -32601, "Method not found: $method")
                } else {
                    rpcResult(
                        id,
                        mapOf(
                            "resultType" to "complete",
                            "supportedVersions" to listOf(MODERN_PROTOCOL_VERSION),
                            "capabilities" to mapOf("tools" to mapOf("listChanged" to false)),
                            "ttlMs" to CACHE_TTL_MS,
                            "cacheScope" to "public"
                        ),
                        modern = true
                    )
                }
            }
            "tools/list" -> {
                val tools = listOf(
                    mapOf(
                        "name" to "getApiList",
                        "description" to "Get a list of all available APIs with their title, description, and category. This is useful for discovering what APIs are available.",
                        "inputSchema" to mapOf(
                            "type" to "object",
                            "properties" to mapOf(
                                "category" to mapOf(
                                    "type" to "string",
                                    "description" to "Optional category to filter APIs (e.g., 'USER', 'ES', 'LOTTO')"
                                )
                            )
                        )
                    ),
                    mapOf(
                        "name" to "getApiDetail",
                        "description" to "Get full detailed information for a specific API by its title. Use getApiList first to find the exact title.",
                        "inputSchema" to mapOf(
                            "type" to "object",
                            "properties" to mapOf(
                                "title" to mapOf(
                                    "type" to "string",
                                    "description" to "The exact title of the API as returned by getApiList"
                                )
                            ),
                            "required" to listOf("title")
                        )
                    )
                )
                val result = mapOf("tools" to tools) + if (modernProtocol) {
                    mapOf(
                        "resultType" to "complete",
                        "ttlMs" to CACHE_TTL_MS,
                        "cacheScope" to "public"
                    )
                } else {
                    emptyMap()
                }
                rpcResult(id, result, modernProtocol)
            }
            "tools/call" -> {
                val params = requestBody["params"] as? Map<String, Any?>
                val name = params?.get("name") as? String
                val arguments = params?.get("arguments") as? Map<String, Any?>

                when (name) {
                    "getApiList" -> {
                        val category = arguments?.get("category") as? String
                        val allApis = apiInfoComponent.getAPIInfos()
                        
                        val filtered = if (!category.isNullOrBlank()) {
                            allApis.filter { it.category.equals(category, ignoreCase = true) }
                        } else {
                            allApis
                        }

                        val summaries = filtered.map { 
                            mapOf(
                                "title" to it.title,
                                "description" to it.description,
                                "category" to it.category
                            )
                        }

                        toolCallResult(
                            id,
                            listOf(
                                mapOf(
                                    "type" to "text",
                                    "text" to objectMapper.writeValueAsString(summaries)
                                )
                            ),
                            modernProtocol
                        )
                    }
                    "getApiDetail" -> {
                        val title = arguments?.get("title") as? String
                        if (title.isNullOrBlank()) {
                            return errorResponse(id, -32602, "Title is required")
                        }

                        val api = apiInfoComponent.getAPIInfos().find { it.title.equals(title, ignoreCase = true) }
                        if (api == null) {
                            return errorResponse(id, -32602, "API not found with title: $title")
                        }

                        val securityGuide = """
                            [Apisis API Information]
                            1. Base URL: https://apisis.dev
                            2. Authentication: All requests must include 'X-API-Key' header.
                            3. Security: Never expose the API Key in client-side code (frontend).
                            4. Management: Use environment variables for API keys and do not commit them to version control.

                            [Response Format]
                            All Open APIs return responses wrapped in an Envelope structure:
                            {
                              "title": "API title",
                              "version": "API version",
                              "current": 1,              // Current API call count
                              "limit": 100,              // API call limit (null if unlimited)
                              "timestamp": "2024-01-01T00:00:00",  // Response timestamp (ISO-8601)
                              "payload": { /* Actual API response data */ },
                              "processMs": 123           // Server processing time in milliseconds
                            }

                            The actual API response data is always in the "payload" field.
                        """.trimIndent()

                        toolCallResult(
                            id,
                            listOf(
                                mapOf(
                                    "type" to "text",
                                    "text" to "$securityGuide\n\n${objectMapper.writeValueAsString(api)}"
                                )
                            ),
                            modernProtocol
                        )
                    }
                    else -> errorResponse(id, -32601, "Method not found: $name")
                }
            }
            else -> {
                if (method.startsWith("notifications/")) null
                else errorResponse(id, -32601, "Method not found: $method")
            }
        }
    }

    private fun toolCallResult(
        id: Any?,
        content: List<Map<String, String>>,
        modern: Boolean
    ): Map<String, Any?> {
        val result = mapOf("content" to content) + if (modern) {
            mapOf("resultType" to "complete", "isError" to false)
        } else {
            emptyMap()
        }
        return rpcResult(id, result, modern)
    }

    private fun rpcResult(id: Any?, result: Map<String, Any?>, modern: Boolean): Map<String, Any?> {
        val responseResult = if (modern) {
            mapOf("resultType" to "complete") + result +
                ("_meta" to mapOf("io.modelcontextprotocol/serverInfo" to serverInfo()))
        } else {
            result
        }
        return mapOf("jsonrpc" to "2.0", "id" to id, "result" to responseResult)
    }

    private fun requestProtocolVersion(requestBody: Map<String, Any?>): String? {
        val params = requestBody["params"] as? Map<*, *> ?: return null
        val meta = params["_meta"] as? Map<*, *> ?: return null
        return meta["io.modelcontextprotocol/protocolVersion"] as? String
    }

    private fun negotiatedLegacyProtocolVersion(requestBody: Map<String, Any?>): String {
        val params = requestBody["params"] as? Map<*, *> ?: return LATEST_LEGACY_PROTOCOL_VERSION
        val requestedVersion = params["protocolVersion"] as? String
        return if (requestedVersion != null && requestedVersion in LEGACY_PROTOCOL_VERSIONS) {
            requestedVersion
        } else {
            LATEST_LEGACY_PROTOCOL_VERSION
        }
    }

    private fun serverInfo() = mapOf("name" to "Apisis MCP Server", "version" to "1.0.0")

    private fun errorResponse(id: Any?, code: Int, message: String): Map<String, Any?> {
        return mapOf(
            "jsonrpc" to "2.0",
            "id" to id,
            "error" to mapOf(
                "code" to code,
                "message" to message
            )
        )
    }

    companion object {
        private const val MODERN_PROTOCOL_VERSION = "2026-07-28"
        private const val LATEST_LEGACY_PROTOCOL_VERSION = "2025-11-25"
        private const val CACHE_TTL_MS = 300_000
        private val LEGACY_PROTOCOL_VERSIONS = setOf("2025-11-25", "2025-06-18", "2025-03-26")
    }
}
