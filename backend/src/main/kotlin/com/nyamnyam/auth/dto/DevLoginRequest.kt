package com.nyamnyam.auth.dto

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank

data class DevLoginRequest(
    @field:NotBlank
    @field:Email
    val email: String
)
