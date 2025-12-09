package com.hshim.apisis.user.service

import com.hshim.apisis.user.entity.User
import com.hshim.apisis.user.entity.UserOAuth2Provider
import com.hshim.apisis.user.enums.OAuth2Provider
import com.hshim.apisis.user.repository.UserOAuth2ProviderRepository
import com.hshim.apisis.user.repository.UserRepository
import org.springframework.http.HttpStatus
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest
import org.springframework.security.oauth2.core.user.DefaultOAuth2User
import org.springframework.security.oauth2.core.user.OAuth2User
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate
import org.springframework.web.server.ResponseStatusException
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod

@Service
class CustomOAuth2UserService(
    private val userRepository: UserRepository,
    private val userOAuth2ProviderRepository: UserOAuth2ProviderRepository
) : DefaultOAuth2UserService() {

    override fun loadUser(userRequest: OAuth2UserRequest): OAuth2User {
        val oAuth2User = super.loadUser(userRequest)
        val registrationId = userRequest.clientRegistration.registrationId

        return processOAuth2User(registrationId, oAuth2User, userRequest)
    }

    private fun processOAuth2User(registrationId: String, oAuth2User: OAuth2User, userRequest: OAuth2UserRequest): OAuth2User {
        val provider = when (registrationId) {
            "kakao" -> OAuth2Provider.KAKAO
            "github" -> OAuth2Provider.GITHUB
            "google" -> OAuth2Provider.GOOGLE
            else -> throw ResponseStatusException(HttpStatus.BAD_REQUEST, "not supported")
        }

        val (providerId, userInfo) = when (registrationId) {
            "kakao" -> {
                val kakaoId = oAuth2User.getAttribute<Long>("id")!!.toString()
                kakaoId to User.ofKakao(oAuth2User)
            }
            "github" -> {
                val githubId = oAuth2User.getAttribute<Long>("id")!!.toString()
                val email = getGithubPrimaryEmail(userRequest.accessToken.tokenValue)
                githubId to User.ofGithub(oAuth2User, email)
            }
            "google" -> {
                val googleId = oAuth2User.getAttribute<String>("sub")!!
                googleId to User.ofGoogle(oAuth2User)
            }
            else -> throw ResponseStatusException(HttpStatus.BAD_REQUEST, "not supported")
        }

        val existingProvider = userOAuth2ProviderRepository.findByProviderAndProviderId(provider, providerId)

        val user = if (existingProvider != null) {
            userRepository.findById(existingProvider.userId).orElseThrow()
        } else {
            val existingUser = userRepository.findByEmail(userInfo.email)
            existingUser ?: userRepository.save(userInfo)
        }

        if (existingProvider == null) {
            userOAuth2ProviderRepository.save(
                UserOAuth2Provider(
                    userId = user.id,
                    provider = provider,
                    providerId = providerId
                )
            )
        }

        val modifiableAttributes = oAuth2User.attributes.toMutableMap()
        modifiableAttributes["userId"] = user.id
        modifiableAttributes["loginProvider"] = provider.name

        val userNameAttributeName = when (provider) {
            OAuth2Provider.GOOGLE -> "sub"
            else -> "id"
        }

        return DefaultOAuth2User(oAuth2User.authorities, modifiableAttributes, userNameAttributeName)
    }

    private fun getGithubPrimaryEmail(accessToken: String): String {
        val restTemplate = RestTemplate()
        val headers = HttpHeaders().apply {
            set("Authorization", "Bearer $accessToken")
            set("Accept", "application/vnd.github.v3+json")
        }

        val entity = HttpEntity<String>(headers)
        val response = restTemplate.exchange(
            "https://api.github.com/user/emails",
            HttpMethod.GET,
            entity,
            List::class.java
        )

        val emails = response.body as? List<*> ?: return ""

        return emails
            .mapNotNull { it as? Map<*, *> }
            .firstOrNull { it["primary"] == true }
            ?.get("email") as? String
            ?: ""
    }
}
