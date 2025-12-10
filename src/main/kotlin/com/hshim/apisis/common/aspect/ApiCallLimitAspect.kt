package com.hshim.apisis.common.aspect

import com.hshim.apisis.common.annotation.Information
import com.hshim.apisis.common.model.Envelope
import com.hshim.apisis.user.enums.PaymentType
import com.hshim.apisis.web.entity.ApiCallLog
import com.hshim.apisis.web.repository.ApiCallLogRepository
import com.hshim.apisis.web.repository.ApiKeyRepository
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
    private val apiKeyRepository: ApiKeyRepository,
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

        val currentCnt =
            apiCallLogRepository.countByApiKeyValueAndUrlAndMethodAndCalledAtAfter(apiKeyValue, url, httpMethod)

        val paymentType = apiKeyRepository.findPaymentTypeByKeyValue(apiKeyValue)
        val callLimit = when (paymentType) {
            null -> information.callLimitFree

            PaymentType.BASIC_MONTH,
            PaymentType.BASIC_YEAR -> information.callLimitBasic

            PaymentType.PRO_MONTH,
            PaymentType.PRO_YEAR -> information.callLimitPro

            else -> -1
        }

        if (callLimit in 1..currentCnt) throw ResponseStatusException(
            HttpStatus.TOO_MANY_REQUESTS,
            "API call limit exceeded. Limit: $callLimit"
        )

        val startTime = System.currentTimeMillis()
        var isSuccess = true
        var httpStatus = 200
        var errorMessage: String? = null
        var responseTimeMs = 0L

        val result = try {
            val proceedResult = joinPoint.proceed()
            responseTimeMs = System.currentTimeMillis() - startTime
            when (proceedResult) {
                is ResponseEntity<*> -> {
                    httpStatus = proceedResult.statusCode.value()
                    isSuccess = httpStatus in 200..299
                    proceedResult.body
                }

                else -> proceedResult
            }
        } catch (e: Exception) {
            isSuccess = false
            httpStatus = 500
            errorMessage = e.message
            throw e
        } finally {
            Thread {
                try {
                    apiCallLogRepository.save(
                        ApiCallLog(
                            apiKeyValue = apiKeyValue,
                            url = url,
                            method = httpMethod,
                            responseTimeMs = responseTimeMs,
                            isSuccess = isSuccess,
                            httpStatus = httpStatus,
                            errorMessage = errorMessage
                        )
                    )
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }.start()
        }

        return ResponseEntity.ok(Envelope(information, callLimit, currentCnt + 1, result, responseTimeMs))
    }
}
