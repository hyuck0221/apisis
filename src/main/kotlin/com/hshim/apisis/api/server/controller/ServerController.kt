package com.hshim.apisis.api.server.controller

import com.hshim.apisis.api.base.model.ResultResponse
import com.hshim.apisis.common.annotation.Information
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/server")
class ServerController() {
    @Information(
        category = "서버",
        title = "Ping",
        description = "서버 연결 상태를 확인합니다",
        version = "1.0",
    )
    @GetMapping("/ping")
    fun ping(): ResponseEntity<ResultResponse> {
        return ResponseEntity.ok(ResultResponse("pong"))
    }
}
