package com.nyamnyam.auth.oauth

import com.nyamnyam.domain.user.entity.AuthProvider

data class OAuthUserInfo(
    val provider: AuthProvider,
    val providerId: String,
    val email: String,
    val nickname: String
)
