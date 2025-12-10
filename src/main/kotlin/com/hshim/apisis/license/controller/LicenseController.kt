package com.hshim.apisis.license.controller

import com.hshim.apisis.license.model.LicenseRegisterRequest
import com.hshim.apisis.license.model.LicenseResponse
import com.hshim.apisis.license.service.LicenseCommandService
import com.hshim.apisis.license.service.LicenseQueryService
import com.hshim.apisis.user.service.UserUtil.getCurrentUserId
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/web/license")
class LicenseController(
    private val licenseQueryService: LicenseQueryService,
    private val licenseCommandService: LicenseCommandService,
) {
    @GetMapping
    fun findBy(): LicenseResponse? {
        val userId = getCurrentUserId()
        return licenseQueryService.findBy(userId)
            ?.let { LicenseResponse(it) }
    }

    @PostMapping("/register")
    fun register(@RequestBody request: LicenseRegisterRequest): LicenseResponse {
        val userId = getCurrentUserId()
        val license = licenseCommandService.register(userId, request.licenseKey)
        return LicenseResponse(license)
    }

    @DeleteMapping
    fun unregisterLicense() {
        val userId = getCurrentUserId()
        licenseCommandService.unregisterLicense(userId)
    }
}

