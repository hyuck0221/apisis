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
                    .filter { !it.type.name.contains("Model") }

                val requestSchema = mutableMapOf<String, Any>()
                val requestInfos = mutableListOf<FieldInfo>()

                requestParams.forEach { param ->
                    val paramType = detectParameterType(param)

                    when {
                        // Pageable 처리
                        param.type == Pageable::class.java -> {
                            requestInfos.add(FieldInfo("page", "int", "페이지 번호 (0부터 시작)", true, ParameterType.QUERY))
                            requestInfos.add(FieldInfo("size", "int", "페이지 크기", true, ParameterType.QUERY))
                            requestInfos.add(FieldInfo("sort", "String", "정렬 조건 (예: name,asc)", true, ParameterType.QUERY))
                            requestSchema["page"] = "int"
                            requestSchema["size"] = "int"
                            requestSchema["sort"] = "String"
                        }
                        // @PathVariable 처리
                        paramType == ParameterType.PATH -> {
                            val pathVariableName = getPathVariableName(param)
                            val typeName = param.type.simpleName
                            requestInfos.add(FieldInfo(pathVariableName, typeName, "경로 변수", false, ParameterType.PATH))
                            requestSchema[pathVariableName] = typeName
                        }
                        // 일반 파라미터 처리
                        else -> {
                            val fields = extractFieldInfos(param.type, paramType)
                            requestInfos.addAll(fields)
                            requestSchema.putAll(buildSchema(param.type))
                        }
                    }
                }

                val resType = extractActualResponseClass(handlerMethod.method.genericReturnType)
                val responseSchema = buildSchemaFromType(resType)
                val responseInfos = extractFieldInfosFromType(resType, null)

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

    private fun getPathVariableName(param: Parameter): String {
        val pathVariableAnnotation = param.getAnnotation(PathVariable::class.java)
        return when {
            pathVariableAnnotation.value.isNotEmpty() -> pathVariableAnnotation.value
            pathVariableAnnotation.name.isNotEmpty() -> pathVariableAnnotation.name
            else -> param.name
        }
    }

    private fun extractActualResponseClass(returnType: Type): Type {
        return when (returnType) {
            is ParameterizedType -> {
                val rawType = returnType.rawType as Class<*>

                if (rawType.name.contains("ResponseEntity")) {
                    val innerType = returnType.actualTypeArguments.firstOrNull()
                    return extractActualResponseClass(innerType ?: Any::class.java)
                }

                // Page나 List는 전체 타입을 반환 (래퍼 정보 포함)
                returnType
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

    private fun buildSchemaFromType(type: Type): Any {
        return when (type) {
            is ParameterizedType -> {
                val rawType = type.rawType as Class<*>
                if (rawType == Page::class.java || rawType == List::class.java) {
                    val innerType = type.actualTypeArguments.firstOrNull()
                    val innerSchema = if (innerType is Class<*>) {
                        buildSchema(innerType)
                    } else if (innerType is ParameterizedType) {
                        buildSchemaFromType(innerType)
                    } else {
                        mapOf("item" to "Any")
                    }

                    // Page는 래퍼 정보 포함, List는 배열로 직접 표현
                    if (rawType == Page::class.java) {
                        mapOf(
                            "content" to listOf(innerSchema),
                            "pageable" to "Pageable",
                            "totalPages" to "int",
                            "totalElements" to "long",
                            "last" to "boolean",
                            "size" to "int",
                            "number" to "int",
                            "numberOfElements" to "int",
                            "first" to "boolean",
                            "empty" to "boolean"
                        )
                    } else {
                        // List<Object>는 배열로 직접 반환
                        listOf(innerSchema)
                    }
                } else {
                    buildSchema(rawType)
                }
            }
            is Class<*> -> buildSchema(type)
            else -> emptyMap<String, Any>()
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
                            val raw = javaType.rawType as Class<*>
                            if (raw == List::class.java) {
                                val innerType = javaType.actualTypeArguments.firstOrNull()
                                if (innerType is Class<*> && !isSimpleType(innerType)) {
                                    // List<Object>의 경우 객체 스키마를 배열로 표현
                                    listOf(buildSchema(innerType))
                                } else {
                                    val args = if (innerType != null) getSimpleTypeName(innerType) else "Any"
                                    "List<$args>"
                                }
                            } else {
                                val rawName = raw.simpleName
                                val innerType = javaType.actualTypeArguments.firstOrNull()
                                val args = if (innerType != null) getSimpleTypeName(innerType) else "Any"
                                "$rawName<$args>"
                            }
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

    private fun extractFieldInfosFromType(
        type: Type,
        paramType: ParameterType?,
        prefix: String = ""
    ): List<FieldInfo> {
        return when (type) {
            is ParameterizedType -> {
                val rawType = type.rawType as Class<*>
                if (rawType == Page::class.java || rawType == List::class.java) {
                    val innerType = type.actualTypeArguments.firstOrNull()
                    val clazz = when (innerType) {
                        is Class<*> -> innerType
                        is ParameterizedType -> innerType.rawType as Class<*>
                        else -> Any::class.java
                    }

                    val fields = mutableListOf<FieldInfo>()

                    // Page 래퍼 필드 추가
                    if (rawType == Page::class.java) {
                        fields.add(FieldInfo("content", rawType.simpleName, "페이지 컨텐츠", false, paramType))
                        fields.add(FieldInfo("totalPages", "int", "전체 페이지 수", false, paramType))
                        fields.add(FieldInfo("totalElements", "long", "전체 요소 수", false, paramType))
                        fields.add(FieldInfo("size", "int", "페이지 크기", false, paramType))
                        fields.add(FieldInfo("number", "int", "현재 페이지 번호", false, paramType))
                    }

                    // 내부 객체 필드 추가
                    fields.addAll(extractFieldInfos(clazz, paramType, if (rawType == Page::class.java) "content" else "items"))
                    fields
                } else {
                    extractFieldInfos(rawType, paramType, prefix)
                }
            }
            is Class<*> -> extractFieldInfos(type, paramType, prefix)
            else -> emptyList()
        }
    }
}