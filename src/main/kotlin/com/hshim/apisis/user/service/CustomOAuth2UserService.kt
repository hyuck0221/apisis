package com.hshim.apisis.user.service

import com.hshim.apisis.user.entity.User
import com.hshim.apisis.user.enums.OAuth2Provider
import com.hshim.apisis.user.repository.UserRepository
import org.springframework.http.HttpStatus
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest
import org.springframework.security.oauth2.core.user.DefaultOAuth2User
import org.springframework.security.oauth2.core.user.OAuth2User
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException

@Service
class CustomOAuth2UserService(
    private val userRepository: UserRepository
) : DefaultOAuth2UserService() {

    override fun loadUser(userRequest: OAuth2UserRequest): OAuth2User {
        val oAuth2User = super.loadUser(userRequest)
        val registrationId = userRequest.clientRegistration.registrationId

        return processOAuth2User(registrationId, oAuth2User)
    }

    private fun processOAuth2User(registrationId: String, oAuth2User: OAuth2User): OAuth2User {
        val userInfo = when (registrationId) {
            "kakao" -> User.ofKakao(oAuth2User)
            else -> throw ResponseStatusException(HttpStatus.BAD_REQUEST, "not supported")
        }

        val user = userRepository.findByProviderAndProviderId(userInfo.provider, userInfo.providerId)
            ?: userRepository.save(userInfo)

        val modifiableAttributes = oAuth2User.attributes.toMutableMap()
        modifiableAttributes["userId"] = user.id

        return DefaultOAuth2User(oAuth2User.authorities, modifiableAttributes, "id")
    }
}
