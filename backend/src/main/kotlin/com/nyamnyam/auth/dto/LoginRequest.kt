package com.nyamnyam.auth.dto

import jakarta.validation.constraints.NotBlank

data class LoginRequest(
    @field:NotBlank(message = "Authorization code is required")
    val code: String,

    @field:NotBlank(message = "Redirect URI is required")
    val redirectUri: String,

    val state: String? = null
)
