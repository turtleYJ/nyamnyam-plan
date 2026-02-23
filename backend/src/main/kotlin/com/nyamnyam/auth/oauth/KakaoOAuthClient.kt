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
class KakaoOAuthClient(
    private val webClient: WebClient,
    private val oAuthProperties: OAuthProperties
) : OAuthClient {

    override val provider = AuthProvider.KAKAO

    override fun getUserInfo(code: String, redirectUri: String, state: String?): OAuthUserInfo {
        val tokenResponse = exchangeCodeForToken(code, redirectUri)
        return fetchUserInfo(tokenResponse.accessToken)
    }

    private fun exchangeCodeForToken(code: String, redirectUri: String): KakaoTokenResponse {
        return webClient.post()
            .uri(oAuthProperties.kakao.tokenUri)
            .contentType(MediaType.APPLICATION_FORM_URLENCODED)
            .bodyValue(
                "grant_type=authorization_code" +
                "&client_id=${oAuthProperties.kakao.clientId}" +
                "&client_secret=${oAuthProperties.kakao.clientSecret}" +
                "&redirect_uri=$redirectUri" +
                "&code=$code"
            )
            .retrieve()
            .bodyToMono(KakaoTokenResponse::class.java)
            .block() ?: throw BusinessException(ErrorCode.OAUTH_AUTHENTICATION_FAILED)
    }

    private fun fetchUserInfo(accessToken: String): OAuthUserInfo {
        val userResponse = webClient.get()
            .uri(oAuthProperties.kakao.userInfoUri)
            .header("Authorization", "Bearer $accessToken")
            .retrieve()
            .bodyToMono(KakaoUserResponse::class.java)
            .block() ?: throw BusinessException(ErrorCode.OAUTH_AUTHENTICATION_FAILED)

        return OAuthUserInfo(
            provider = AuthProvider.KAKAO,
            providerId = userResponse.id.toString(),
            email = userResponse.kakaoAccount?.email ?: "",
            nickname = userResponse.kakaoAccount?.profile?.nickname ?: "User"
        )
    }

    private data class KakaoTokenResponse(
        @JsonProperty("access_token") val accessToken: String,
        @JsonProperty("token_type") val tokenType: String? = null
    )

    private data class KakaoUserResponse(
        val id: Long,
        @JsonProperty("kakao_account") val kakaoAccount: KakaoAccount? = null
    )

    private data class KakaoAccount(
        val email: String? = null,
        val profile: KakaoProfile? = null
    )

    private data class KakaoProfile(
        val nickname: String? = null
    )
}
