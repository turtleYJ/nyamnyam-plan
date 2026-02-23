package com.nyamnyam.auth.oauth

import com.fasterxml.jackson.annotation.JsonProperty
import com.nyamnyam.common.exception.BusinessException
import com.nyamnyam.common.exception.ErrorCode
import com.nyamnyam.config.OAuthProperties
import com.nyamnyam.domain.user.entity.AuthProvider
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient

@Component
class NaverOAuthClient(
    private val webClient: WebClient,
    private val oAuthProperties: OAuthProperties
) : OAuthClient {

    override val provider = AuthProvider.NAVER

    override fun getUserInfo(code: String, redirectUri: String, state: String?): OAuthUserInfo {
        val tokenResponse = exchangeCodeForToken(code, state)
        return fetchUserInfo(tokenResponse.accessToken)
    }

    private fun exchangeCodeForToken(code: String, state: String?): NaverTokenResponse {
        return webClient.post()
            .uri(oAuthProperties.naver.tokenUri)
            .contentType(MediaType.APPLICATION_FORM_URLENCODED)
            .bodyValue(
                "grant_type=authorization_code" +
                "&client_id=${oAuthProperties.naver.clientId}" +
                "&client_secret=${oAuthProperties.naver.clientSecret}" +
                "&code=$code" +
                "&state=${state ?: ""}"
            )
            .retrieve()
            .bodyToMono(NaverTokenResponse::class.java)
            .block() ?: throw BusinessException(ErrorCode.OAUTH_AUTHENTICATION_FAILED)
    }

    private fun fetchUserInfo(accessToken: String): OAuthUserInfo {
        val userResponse = webClient.get()
            .uri(oAuthProperties.naver.userInfoUri)
            .header("Authorization", "Bearer $accessToken")
            .retrieve()
            .bodyToMono(NaverUserResponse::class.java)
            .block() ?: throw BusinessException(ErrorCode.OAUTH_AUTHENTICATION_FAILED)

        val profile = userResponse.response

        return OAuthUserInfo(
            provider = AuthProvider.NAVER,
            providerId = profile.id,
            email = profile.email ?: "",
            nickname = profile.nickname ?: profile.name ?: "User"
        )
    }

    private data class NaverTokenResponse(
        @JsonProperty("access_token") val accessToken: String,
        @JsonProperty("token_type") val tokenType: String? = null
    )

    private data class NaverUserResponse(
        val resultcode: String? = null,
        val message: String? = null,
        val response: NaverProfile
    )

    private data class NaverProfile(
        val id: String,
        val email: String? = null,
        val nickname: String? = null,
        val name: String? = null
    )
}
