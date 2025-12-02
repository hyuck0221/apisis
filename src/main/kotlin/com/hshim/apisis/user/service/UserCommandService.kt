package com.hshim.apisis.user.service

import com.hshim.apisis.auth.repository.ApiKeyRepository
import com.hshim.apisis.user.repository.UserRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class UserCommandService(
    private val userRepository: UserRepository,
    private val apiKeyRepository: ApiKeyRepository,
) {
    fun delete(id: String) {
        apiKeyRepository.deleteAllByUserId(id)
        userRepository.deleteById(id)
    }
}