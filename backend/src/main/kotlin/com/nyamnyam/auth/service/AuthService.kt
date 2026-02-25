package com.nyamnyam.auth.service

import com.nyamnyam.auth.dto.LoginRequest
import com.nyamnyam.auth.dto.RefreshRequest
import com.nyamnyam.auth.dto.TokenResponse
import com.nyamnyam.auth.jwt.JwtTokenProvider
import com.nyamnyam.auth.oauth.OAuthClient
import com.nyamnyam.auth.oauth.OAuthUserInfo
import com.nyamnyam.common.exception.BusinessException
import com.nyamnyam.common.exception.ErrorCode
import com.nyamnyam.config.JwtProperties
import com.nyamnyam.domain.user.entity.AuthProvider
import com.nyamnyam.domain.user.entity.User
import com.nyamnyam.domain.user.repository.UserRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class AuthService(
    private val userRepository: UserRepository,
    private val jwtTokenProvider: JwtTokenProvider,
    private val jwtProperties: JwtProperties,
    oAuthClients: List<OAuthClient>
) {
    private val oAuthClientMap: Map<AuthProvider, OAuthClient> =
        oAuthClients.associateBy { it.provider }

    fun login(provider: AuthProvider, request: LoginRequest): TokenResponse {
        val oAuthClient = oAuthClientMap[provider]
            ?: throw BusinessException(ErrorCode.UNSUPPORTED_OAUTH_PROVIDER)

        val userInfo = oAuthClient.getUserInfo(
            code = request.code,
            redirectUri = request.redirectUri,
            state = request.state
        )

        val user = userRepository.findByProviderAndProviderId(userInfo.provider, userInfo.providerId)
            ?: createUser(userInfo)

        return generateTokens(user)
    }

    fun refresh(request: RefreshRequest): TokenResponse {
        if (!jwtTokenProvider.validateToken(request.refreshToken)) {
            throw BusinessException(ErrorCode.INVALID_TOKEN)
        }

        val userId = jwtTokenProvider.getUserIdFromToken(request.refreshToken)
        val storedToken = jwtTokenProvider.getRefreshToken(userId)

        if (storedToken != request.refreshToken) {
            throw BusinessException(ErrorCode.INVALID_TOKEN)
        }

        val user = userRepository.findById(userId)
            .orElseThrow { BusinessException(ErrorCode.USER_NOT_FOUND) }

        return generateTokens(user)
    }

    fun devLogin(email: String): TokenResponse {
        val user = userRepository.findByEmail(email)
            ?: userRepository.save(
                User(
                    email = email,
                    nickname = email.substringBefore("@"),
                    provider = AuthProvider.DEV,
                    providerId = "dev-$email"
                )
            )
        return generateTokens(user)
    }

    fun logout(userId: Long) {
        jwtTokenProvider.deleteRefreshToken(userId)
    }

    private fun createUser(userInfo: OAuthUserInfo): User {
        return userRepository.save(
            User(
                email = userInfo.email,
                nickname = userInfo.nickname,
                provider = userInfo.provider,
                providerId = userInfo.providerId
            )
        )
    }

    private fun generateTokens(user: User): TokenResponse {
        val accessToken = jwtTokenProvider.generateAccessToken(user.id, user.email)
        val refreshToken = jwtTokenProvider.generateRefreshToken(user.id)
        jwtTokenProvider.saveRefreshToken(user.id, refreshToken)

        return TokenResponse(
            accessToken = accessToken,
            refreshToken = refreshToken,
            expiresIn = jwtProperties.accessTokenExpiry / 1000
        )
    }
}
