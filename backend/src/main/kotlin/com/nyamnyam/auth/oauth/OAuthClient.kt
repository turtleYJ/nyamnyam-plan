package com.nyamnyam.auth.oauth

import com.nyamnyam.domain.user.entity.AuthProvider

interface OAuthClient {
    val provider: AuthProvider
    fun getUserInfo(code: String, redirectUri: String, state: String? = null): OAuthUserInfo
}
