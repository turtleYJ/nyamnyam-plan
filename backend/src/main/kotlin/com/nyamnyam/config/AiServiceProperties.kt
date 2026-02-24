package com.nyamnyam.config

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "ai.service")
data class AiServiceProperties(
    val url: String = "http://localhost:8000",
    val timeout: Long = 30000
)
