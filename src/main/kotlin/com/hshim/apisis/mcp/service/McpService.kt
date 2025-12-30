package com.hshim.apisis.mcp.service

import com.fasterxml.jackson.databind.ObjectMapper
import com.hshim.apisis.common.util.APIInfoComponent
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.util.concurrent.ConcurrentHashMap

@Service
class McpService(
    private val apiInfoComponent: APIInfoComponent,
    private val objectMapper: ObjectMapper
) {
    private val logger = LoggerFactory.getLogger(McpService::class.java)

    // 세션별 SSE Emitter 관리 (간단한 구현)
    // 실제 운영 환경에서는 메모리 누수 방지 로직 필요
    // private val emitters = ConcurrentHashMap<String, SseEmitter>() 
    // -> Controller에서 처리

    fun handleMessage(requestBody: Map<String, Any>): Map<String, Any?>? {
        val method = requestBody["method"] as? String ?: return null
        val id = requestBody["id"]

        logger.info("MCP Request: method=$method, id=$id")

        return when (method) {
            "initialize" -> {
                mapOf(
                    "jsonrpc" to "2.0",
                    "id" to id,
                    "result" to mapOf(
                        "protocolVersion" to "2024-11-05",
                        "capabilities" to mapOf(
                            "tools" to mapOf("listChanged" to true)
                        ),
                        "serverInfo" to mapOf(
                            "name" to "Apisis MCP Server",
                            "version" to "1.0.0"
                        )
                    )
                )
            }
            "notifications/initialized" -> {
                // 초기화 완료 알림은 응답 없음
                null
            }
            "tools/list" -> {
                mapOf(
                    "jsonrpc" to "2.0",
                    "id" to id,
                    "result" to mapOf(
                        "tools" to listOf(
                            mapOf(
                                "name" to "getApiInfo",
                                "description" to "Discover available APIs provided by this server. You can filter by category.",
                                "inputSchema" to mapOf(
                                    "type" to "object",
                                    "properties" to mapOf(
                                        "category" to mapOf(
                                            "type" to "string"
                                        )
                                    )
                                )
                            )
                        )
                    )
                )
            }
            "tools/call" -> {
                val params = requestBody["params"] as? Map<String, Any?>
                val name = params?.get("name") as? String
                val arguments = params?.get("arguments") as? Map<String, Any?>

                if (name == "getApiInfo") {
                    val category = arguments?.get("category") as? String
                    val allApis = apiInfoComponent.getAPIInfos()
                    
                    val filtered = if (!category.isNullOrBlank()) {
                        allApis.filter { it.category.equals(category, ignoreCase = true) }
                    } else {
                        allApis
                    }

                    mapOf(
                        "jsonrpc" to "2.0",
                        "id" to id,
                        "result" to mapOf(
                            "content" to listOf(
                                mapOf(
                                    "type" to "text",
                                    "text" to objectMapper.writeValueAsString(filtered)
                                )
                            )
                        )
                    )
                } else {
                    errorResponse(id, -32601, "Method not found")
                }
            }
            else -> {
                // ping 등 기타 메서드는 무시하거나 에러
                if (method.startsWith("$")) null // JSON-RPC 내부 메서드 무시
                else errorResponse(id, -32601, "Method not found: $method")
            }
        }
    }

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
}
