package com.hshim.apisis.common.aspect

import com.hshim.apisis.auth.entity.ApiCallLog
import com.hshim.apisis.auth.repository.ApiCallLogRepository
import com.hshim.apisis.common.annotation.Information
import com.hshim.apisis.common.model.Envelope
import jakarta.servlet.http.HttpServletRequest
import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.reflect.MethodSignature
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.server.ResponseStatusException

@Aspect
@Component
class ApiCallLimitAspect(
    private val apiCallLogRepository: ApiCallLogRepository,
    private val request: HttpServletRequest
) {

    @Around("@annotation(com.hshim.apisis.common.annotation.Information)")
    @Transactional
    fun checkCallLimit(joinPoint: ProceedingJoinPoint): ResponseEntity<Envelope<Any>> {
        val signature = joinPoint.signature as MethodSignature
        val method = signature.method
        val information = method.getAnnotation(Information::class.java)

        val authentication = SecurityContextHolder.getContext().authentication
        val apiKeyValue = authentication?.principal as String

        val url = request.requestURI
        val httpMethod = request.method

        val currentCnt = apiCallLogRepository.countByApiKeyValueAndUrlAndMethodAndCalledAtAfter(apiKeyValue, url, httpMethod)

        if (information.callLimit in 1..currentCnt) throw ResponseStatusException(
            HttpStatus.TOO_MANY_REQUESTS,
            "API call limit exceeded. Limit: ${information.callLimit}"
        )

        Thread {
            apiCallLogRepository.save(
                ApiCallLog(
                    apiKeyValue = apiKeyValue,
                    url = url,
                    method = httpMethod
                )
            )
        }.start()

        val result = when (val result = joinPoint.proceed()) {
            is ResponseEntity<*> -> result.body
            else -> result
        }

        return ResponseEntity.ok(Envelope(information, currentCnt + 1, result))
    }
}
