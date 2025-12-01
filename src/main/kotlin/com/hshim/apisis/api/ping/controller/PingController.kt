package com.hshim.apisis.api.ping.controller

import com.hshim.apisis.common.annotation.Information
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/ping")
class PingController() {

    @Information(
        title = "check server status",
        version = "1.0",
    )
    @GetMapping
    fun ping(): ResponseEntity<Map<String, String>> {
        return ResponseEntity.ok(mapOf("result" to "pong"))
    }
}
