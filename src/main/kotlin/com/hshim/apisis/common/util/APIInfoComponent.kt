package com.hshim.apisis.common.util

import com.hshim.apisis.common.annotation.FieldDescription
import com.hshim.apisis.common.annotation.Information
import com.hshim.apisis.common.model.APIInfoResponse
import com.hshim.apisis.common.model.FieldInfo
import com.hshim.apisis.common.model.ParameterType
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Component
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.method.HandlerMethod
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping
import java.lang.reflect.Parameter
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type
import kotlin.reflect.KClass
import kotlin.reflect.KProperty1
import kotlin.reflect.full.findAnnotation
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

                val requestParams = handlerMethod.method.parameters
                    .filter { !it.type.name.contains("Model") && it.type != Pageable::class.java }

                val requestSchema = mutableMapOf<String, Any>()
                val requestInfos = mutableListOf<FieldInfo>()

                requestParams.forEach { param ->
                    val paramType = detectParameterType(param)
                    val fields = extractFieldInfos(param.type, paramType)
                    requestInfos.addAll(fields)
                    requestSchema.putAll(buildSchema(param.type))
                }

                val resClass = extractActualResponseClass(handlerMethod.method.genericReturnType)
                val responseSchema = buildSchema(resClass)
                val responseInfos = extractFieldInfos(resClass, null)

                result.add(
                    APIInfoResponse(
                        url = mapping.patternValues.first(),
                        method = mapping.methodsCondition.methods.first().name,
                        information = info,
                        requestSchema = requestSchema,
                        responseSchema = responseSchema,
                        requestInfos = requestInfos,
                        responseInfos = responseInfos
                    )
                )
            }
        }

        apiInfoCache = result
        return result
    }

    private fun detectParameterType(param: Parameter): ParameterType {
        return when {
            param.isAnnotationPresent(RequestBody::class.java) -> ParameterType.BODY
            param.isAnnotationPresent(PathVariable::class.java) -> ParameterType.PATH
            param.isAnnotationPresent(RequestHeader::class.java) -> ParameterType.HEADER
            param.isAnnotationPresent(RequestParam::class.java) -> ParameterType.QUERY
            else -> ParameterType.QUERY
        }
    }

    private fun extractActualResponseClass(returnType: Type): Class<*> {
        return when (returnType) {
            is ParameterizedType -> {
                val rawType = returnType.rawType as Class<*>

                if (rawType.name.contains("ResponseEntity")) {
                    val innerType = returnType.actualTypeArguments.firstOrNull()
                    return extractActualResponseClass(innerType ?: Any::class.java)
                }

                if (rawType == Page::class.java || rawType == List::class.java) {
                    val actualType = returnType.actualTypeArguments.firstOrNull()
                    return (actualType as? Class<*>) ?: Any::class.java
                }

                rawType
            }
            is Class<*> -> returnType
            else -> Any::class.java
        }
    }

    private fun extractFieldInfos(
        clazz: Class<*>,
        paramType: ParameterType?,
        prefix: String = ""
    ): List<FieldInfo> {
        val fields = mutableListOf<FieldInfo>()

        try {
            val kClass = clazz.kotlin
            kClass.memberProperties.forEach { prop ->
                val path = if (prefix.isEmpty()) prop.name else "$prefix.${prop.name}"
                val type = prop.returnType
                val nullable = type.isMarkedNullable
                val description = prop.findAnnotation<FieldDescription>()?.description ?: ""

                val typeClass = getTypeClass(type.javaType)

                if (isSimpleType(typeClass)) {
                    fields.add(
                        FieldInfo(
                            path = path,
                            type = getSimpleTypeName(type.javaType),
                            description = description,
                            nullable = nullable,
                            parameterType = paramType
                        )
                    )
                } else {
                    fields.add(
                        FieldInfo(
                            path = path,
                            type = typeClass.simpleName,
                            description = description,
                            nullable = nullable,
                            parameterType = paramType
                        )
                    )
                    fields.addAll(extractFieldInfos(typeClass, paramType, path))
                }
            }
        } catch (e: Exception) {
            // Kotlin reflection이 실패하는 경우 무시
        }

        return fields
    }

    private fun getTypeClass(javaType: Type): Class<*> {
        return when (javaType) {
            is Class<*> -> javaType
            is ParameterizedType -> {
                val rawType = javaType.rawType as Class<*>
                if (rawType == List::class.java || rawType == Page::class.java) {
                    val actualType = javaType.actualTypeArguments.firstOrNull()
                    (actualType as? Class<*>) ?: Any::class.java
                } else {
                    rawType
                }
            }
            else -> Any::class.java
        }
    }

    private fun isSimpleType(clazz: Class<*>): Boolean {
        return clazz.isPrimitive ||
               clazz.packageName.startsWith("java.") ||
               clazz.isEnum ||
               clazz == String::class.java ||
               Number::class.java.isAssignableFrom(clazz) ||
               clazz == Boolean::class.java
    }

    private fun getSimpleTypeName(javaType: Type): String {
        return when (javaType) {
            is Class<*> -> javaType.simpleName
            is ParameterizedType -> {
                val raw = (javaType.rawType as Class<*>).simpleName
                val args = javaType.actualTypeArguments.joinToString(", ") {
                    getSimpleTypeName(it)
                }
                "$raw<$args>"
            }
            else -> javaType.typeName
        }
    }

    private fun buildSchema(clazz: Class<*>): Map<String, Any> {
        return try {
            clazz.kotlin.memberProperties.associate { prop ->
                val name = prop.name
                val type = prop.returnType.javaType

                fun parseType(javaType: Type): Any {
                    return when (javaType) {
                        is Class<*> -> {
                            if (isSimpleType(javaType)) {
                                javaType.simpleName
                            } else {
                                buildSchema(javaType)
                            }
                        }
                        is ParameterizedType -> {
                            val raw = javaType.rawType.typeName
                            val innerType = javaType.actualTypeArguments.firstOrNull()
                            val args = if (innerType != null) parseType(innerType) else "Any"
                            "${raw.split(".").last()}<$args>"
                        }
                        else -> javaType.typeName
                    }
                }

                name to parseType(type)
            }
        } catch (e: Exception) {
            emptyMap()
        }
    }
}