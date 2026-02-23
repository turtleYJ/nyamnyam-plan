package com.nyamnyam.config

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "oauth")
data class OAuthProperties(
    val kakao: OAuthProviderProperties,
    val naver: OAuthProviderProperties
)

data class OAuthProviderProperties(
    val clientId: String,
    val clientSecret: String,
    val tokenUri: String,
    val userInfoUri: String
)
