package com.hshim.apisis.api.random.controller

import com.hshim.apisis.api.random.model.GenerateRandomNumberCondition
import com.hshim.apisis.api.random.model.GenerateRandomStringCondition
import com.hshim.apisis.api.random.model.RandomNumberResponse
import com.hshim.apisis.api.random.model.RandomStringResponse
import com.hshim.apisis.api.random.service.RandomQueryService
import com.hshim.apisis.common.annotation.Information
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/random")
class RandomController(private val randomQueryService: RandomQueryService) {
    @Information(
        category = "랜덤",
        title = "랜덤 정수 생성",
        description = "지정한 범위 내 랜덤 정수를 생성합니다",
        version = "1.0",
        callLimitFree = 500,
        callLimitBasic = 10000,
        callLimitPro = 2500000
    )
    @GetMapping("/number")
    fun generateNumber(condition: GenerateRandomNumberCondition): ResponseEntity<RandomNumberResponse> {
        return ResponseEntity.ok(randomQueryService.generateNumber(condition))
    }

    @Information(
        category = "랜덤",
        title = "UUID",
        description = "UUID를 생성합니다",
        version = "1.0",
        callLimitFree = 5000,
        callLimitBasic = 100000,
        callLimitPro = 25000000
    )
    @GetMapping("/uuid")
    fun generateUUID(condition: GenerateRandomStringCondition): ResponseEntity<RandomStringResponse> {
        return ResponseEntity.ok(randomQueryService.generateUUID(condition))
    }

    @Information(
        category = "랜덤",
        title = "ULID",
        description = "ULID를 생성합니다",
        version = "1.0",
        callLimitFree = 5000,
        callLimitBasic = 100000,
        callLimitPro = 25000000
    )
    @GetMapping("/ulid")
    fun generateULID(condition: GenerateRandomStringCondition): ResponseEntity<RandomStringResponse> {
        return ResponseEntity.ok(randomQueryService.generateULID(condition))
    }
}