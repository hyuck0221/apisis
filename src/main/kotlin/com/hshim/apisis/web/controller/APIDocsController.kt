package com.hshim.apisis.web.controller

import com.hshim.apisis.common.model.APIInfoResponse
import com.hshim.apisis.common.util.APIInfoComponent
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/web/docs")
class APIDocsController(
    private val apiInfoComponent: APIInfoComponent,
) {
    @GetMapping("/list")
    fun getAPIList(): List<APIInfoResponse> {
        return apiInfoComponent.getAPIInfos()
    }
}