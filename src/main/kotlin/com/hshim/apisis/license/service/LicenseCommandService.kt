package com.hshim.apisis.license.service

import com.hshim.apisis.license.entity.License
import com.hshim.apisis.license.repository.LicenseRepository
import com.hshim.apisis.user.enums.PaymentType
import com.hshim.apisis.user.repository.UserRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.server.ResponseStatusException
import java.time.LocalDateTime

@Service
@Transactional
class LicenseCommandService(
    private val licenseRepository: LicenseRepository,
    private val userRepository: UserRepository,
) {
    fun register(userId: String, licenseKey: String): License {
        val license = licenseRepository.findByIdOrNull(licenseKey)
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "license not found")

        if (userRepository.existsByLicenseId(licenseKey))
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "The license is already in use")

        if (license.expiredDate != null && license.expiredDate!!.isBefore(LocalDateTime.now()))
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "The license is expired")

        userRepository.findByIdOrNull(userId)
            ?.apply { this.license = license }
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "not found user")

        if (license.expiredDate == null) license.expiredDate = calculateExpiredDate(license.paymentType)
        return license
    }

    fun unregisterLicense(userId: String) {
        userRepository.findByIdOrNull(userId)
            ?.apply { this.license = null }
            ?: throw IllegalArgumentException("not found user")
    }

    private fun calculateExpiredDate(paymentType: PaymentType): LocalDateTime? {
        val now = LocalDateTime.now()
        return when (paymentType) {
            PaymentType.BASIC_MONTH, PaymentType.PRO_MONTH, PaymentType.ENTERPRISE_MONTH -> now.plusMonths(1)
            PaymentType.BASIC_YEAR, PaymentType.PRO_YEAR, PaymentType.ENTERPRISE_YEAR -> now.plusYears(1)
            PaymentType.EXTRA -> null
        }
    }
}
