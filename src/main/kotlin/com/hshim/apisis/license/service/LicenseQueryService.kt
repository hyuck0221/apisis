package com.hshim.apisis.license.service

import com.hshim.apisis.license.entity.License
import com.hshim.apisis.user.repository.UserRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class LicenseQueryService(private val userRepository: UserRepository) {
    fun findBy(userId: String): License? {
        return userRepository.findByIdOrNull(userId)
            ?.license
            ?: throw IllegalArgumentException("사용자를 찾을 수 없습니다")
    }
}
