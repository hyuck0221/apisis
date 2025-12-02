package com.hshim.apisis.common.util

import com.hshim.apisis.common.annotation.Information
import com.hshim.apisis.common.model.APIInfoResponse
import org.springframework.stereotype.Component
import org.springframework.web.method.HandlerMethod
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type
import kotlin.reflect.full.memberProperties
import kotlin.reflect.jvm.javaType

@Component
class APIInfoComponent(private val handlerMapping: RequestMappingHandlerMapping) {

    private var apiInfoCache: List<APIInfoResponse> = emptyList()

    init {
        getAnnotatedApis()
    }

    fun getAPIInfos() = apiInfoCache.takeIf { it.isNotEmpty() } ?: getAnnotatedApis()

    private fun getAnnotatedApis(): List<APIInfoResponse> {
        val result = mutableListOf<APIInfoResponse>()

        handlerMapping.handlerMethods.forEach { (mapping, handlerMethod: HandlerMethod) ->
            val info = handlerMethod.getMethodAnnotation(Information::class.java)
            if (info != null) {

                val reqClass = handlerMethod.method.parameters
                    .firstOrNull { !it.type.name.contains("Model") }
                    ?.type
                val requestSchema = reqClass?.let { buildSchema(it) } ?: emptyMap()

                val resClass = when (val generic = handlerMethod.method.genericReturnType) {
                    is ParameterizedType -> {
                        val actual = generic.actualTypeArguments.firstOrNull()
                        if (actual is Class<*>) actual else handlerMethod.method.returnType
                    }
                    else -> handlerMethod.method.returnType
                }
                val responseSchema = buildSchema(resClass)

                result.add(
                    APIInfoResponse(
                        url = mapping.patternValues.first(),
                        method = mapping.methodsCondition.methods.first().name,
                        information = info,
                        requestSchema = requestSchema,
                        responseSchema = responseSchema
                    )
                )
            }
        }

        apiInfoCache = result
        return result
    }

    private fun buildSchema(clazz: Class<*>): Map<String, Any> {
        return clazz.kotlin.memberProperties.associate { prop ->
            val name = prop.name
            val type = prop.returnType.javaType

            fun parseType(javaType: Type): Any {
                return when (javaType) {
                    is Class<*> -> {
                        if (javaType.isPrimitive || javaType.packageName.startsWith("java.") || javaType.isEnum) {
                            javaType.simpleName
                        } else {
                            buildSchema(javaType)
                        }
                    }
                    is ParameterizedType -> {
                        val raw = javaType.rawType.typeName
                        val args = parseType(javaType.actualTypeArguments.first())
                        "${raw.replace("java.lang.", "").replace("java.util.", "")}<$args>"
                    }
                    else -> javaType.typeName
                }
            }

            name to parseType(type)
        }
    }
}