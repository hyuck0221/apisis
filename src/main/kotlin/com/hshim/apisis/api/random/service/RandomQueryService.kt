package com.hshim.apisis.api.random.service

import com.hshim.apisis.api.random.model.GenerateRandomNumberCondition
import com.hshim.apisis.api.random.model.GenerateRandomStringCondition
import com.hshim.apisis.api.random.model.RandomNumberResponse
import com.hshim.apisis.api.random.model.RandomStringResponse
import com.hshim.apisis.properties.RandomProperties
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.client.RestTemplate
import org.springframework.web.server.ResponseStatusException
import util.CommonUtil.ulid
import java.util.*

@Service
@Transactional
class RandomQueryService(private val randomProperties: RandomProperties) {

    private val restTemplate = RestTemplate()

    fun generateNumber(condition: GenerateRandomNumberCondition): RandomNumberResponse {
        return try {
            val numbers = restTemplate.exchange(
                "${randomProperties.url}/integers?num=${condition.cnt ?: 1}&min=${condition.min}&max=${condition.max}&col=1&base=10&format=plain",
                HttpMethod.GET,
                null,
                String::class.java
            ).body?.trim()?.split("\n")?.map { it.toInt() } ?: listOf(condition.min)
            RandomNumberResponse(numbers)
        } catch (e: Exception) {
            throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR)
        }
    }

    fun generateUUID(condition: GenerateRandomStringCondition): RandomStringResponse {
        val uuids = (1..(condition.cnt ?: 1)).map { UUID.randomUUID().toString() }
        return RandomStringResponse(uuids)
    }

    fun generateULID(condition: GenerateRandomStringCondition): RandomStringResponse {
        val uuids = (1..(condition.cnt ?: 1)).map { ulid() }
        return RandomStringResponse(uuids)
    }
}